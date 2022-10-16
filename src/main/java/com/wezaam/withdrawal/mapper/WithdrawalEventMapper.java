package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import com.wezaam.withdrawal.event.WithdrawalEvent;
import com.wezaam.withdrawal.model.Withdrawal;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class WithdrawalEventMapper {

    public List<WithdrawalEvent> mapWithdrawals(List<Withdrawal> withdrawals) {
        return withdrawals.stream().map(WithdrawalEventMapper::mapWithdrawal).collect(Collectors.toList());
    }

    public WithdrawalEvent mapWithdrawal(Withdrawal withdrawal) {
        return WithdrawalEvent.builder()
                .id(withdrawal.getId())
                .transactionId(withdrawal.getTransactionId())
                .userId(withdrawal.getUserId())
                .withdrawalTypeDto(WithdrawalMapper.mapWithdrawalType(withdrawal.getWithdrawalType()))
                .status(WithdrawalMapper.mapWithdrawalStatus(withdrawal.getStatus()))
                .amount(withdrawal.getAmount())
                .createdAt(withdrawal.getCreatedAt())
                .paymentMethodId(withdrawal.getPaymentMethodId())
                .executeAt(withdrawal.getExecuteAt())
                .build();
    }
}
