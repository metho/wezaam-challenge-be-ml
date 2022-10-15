package com.wezaam.withdrawal.controllers;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.request.WithdrawalRequest;
import com.wezaam.withdrawal.service.UserService;
import com.wezaam.withdrawal.service.WithdrawalService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@RequiredArgsConstructor
public class WithdrawalController {
    private final WithdrawalService withdrawalService;

    @PostMapping("/withdrawals")
    public WithdrawalDto create(@Valid @RequestBody WithdrawalRequest withdrawalRequest) {
        return withdrawalService.createWithdrawal(withdrawalRequest);
    }

    @GetMapping("/withdrawals")
    public List<WithdrawalDto> findAll() {
        return withdrawalService.findAllWithdrawals();
    }
}
