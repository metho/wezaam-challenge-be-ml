package com.wezaam.withdrawal.controllers;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.request.WithdrawalRequest;
import com.wezaam.withdrawal.service.WithdrawalService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
