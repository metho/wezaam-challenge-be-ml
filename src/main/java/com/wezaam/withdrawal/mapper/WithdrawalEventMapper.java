package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.event.WithdrawalEvent;
import com.wezaam.withdrawal.model.Withdrawal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WithdrawalEventMapper {

    public WithdrawalEvent mapToWithdrawalEvent(Withdrawal withdrawal) {
        return WithdrawalEvent.builder()
                .id(withdrawal.getId())
                .transactionId(withdrawal.getTransactionId())
                .userId(withdrawal.getUserId())
                .withdrawalTypeDto(WithdrawalMapper.mapToWithdrawalTypeDto(withdrawal.getWithdrawalType()))
                .status(WithdrawalMapper.mapToWithdrawalStatusDto(withdrawal.getStatus()))
                .amount(withdrawal.getAmount())
                .createdAt(withdrawal.getCreatedAt())
                .paymentMethodId(withdrawal.getPaymentMethodId())
                .executeAt(withdrawal.getExecuteAt())
                .build();
    }
}
