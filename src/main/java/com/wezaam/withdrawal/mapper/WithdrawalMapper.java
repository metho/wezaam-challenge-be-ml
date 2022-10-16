package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import com.wezaam.withdrawal.model.Withdrawal;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class WithdrawalMapper {

    public List<WithdrawalDto> mapWithdrawal(List<Withdrawal> withdrawals) {
        return withdrawals.stream().map(WithdrawalMapper::mapWithdrawal).collect(Collectors.toList());
    }

    public WithdrawalDto mapWithdrawal(Withdrawal withdrawal) {
        return WithdrawalDto.builder()
                .id(withdrawal.getId())
                .transactionId(withdrawal.getTransactionId())
                .userId(withdrawal.getUserId())
                .withdrawalTypeDto(WithdrawalTypeDto.IMMEDIATE)
                .status(withdrawal.getStatus())
                .amount(withdrawal.getAmount())
                .createdAt(withdrawal.getCreatedAt())
                .paymentMethodId(withdrawal.getPaymentMethodId())
                .build();
    }

}
