package com.wezaam.withdrawal.controllers;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.service.WithdrawalService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@RequiredArgsConstructor
public class WithdrawalController {

    private final ApplicationContext context;
    private final UserController userController;

    private final WithdrawalService withdrawalService;

    @PostMapping("/withdrawals")
    public ResponseEntity create(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String paymentMethodId = request.getParameter("paymentMethodId");
        String amount = request.getParameter("amount");
        String executeAt = request.getParameter("executeAt");
        if (userId == null || paymentMethodId == null || amount == null || executeAt == null) {
            return new ResponseEntity("Required params are missing", HttpStatus.BAD_REQUEST);
        }
        try {
            userController.findById(Long.parseLong(userId));
        } catch (Exception e) {
            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
        }
        if (!context.getBean(PaymentMethodRepository.class).findById(Long.parseLong(paymentMethodId)).isPresent()) {
            return new ResponseEntity("Payment method not found", HttpStatus.NOT_FOUND);
        }

        WithdrawalService withdrawalService = context.getBean(WithdrawalService.class);
        Object body;
        if (executeAt.equals("ASAP")) {
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(Long.parseLong(userId));
            withdrawal.setPaymentMethodId(Long.parseLong(paymentMethodId));
            withdrawal.setAmount(new BigDecimal(amount));
            withdrawal.setCreatedAt(Instant.now());
            withdrawal.setStatus(WithdrawalStatus.PENDING);
            withdrawalService.create(withdrawal);
            body = withdrawal;
        } else {
            ScheduledWithdrawal scheduledWithdrawal = new ScheduledWithdrawal();
            scheduledWithdrawal.setUserId(Long.parseLong(userId));
            scheduledWithdrawal.setPaymentMethodId(Long.parseLong(paymentMethodId));
            scheduledWithdrawal.setAmount(new BigDecimal(amount));
            scheduledWithdrawal.setCreatedAt(Instant.now());
            scheduledWithdrawal.setExecuteAt(Instant.parse(executeAt));
            scheduledWithdrawal.setStatus(WithdrawalStatus.PENDING);
            withdrawalService.schedule(scheduledWithdrawal);
            body = scheduledWithdrawal;
        }

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping("/withdrawals")
    public List<WithdrawalDto> findAll() {
        return withdrawalService.findAllWithdrawals();
    }
}
