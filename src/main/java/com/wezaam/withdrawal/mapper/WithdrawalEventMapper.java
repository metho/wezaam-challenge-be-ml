package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.WithdrawalType;
import com.wezaam.withdrawal.event.ScheduledWithdrawalEvent;
import com.wezaam.withdrawal.event.WithdrawalEvent;
import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import com.wezaam.withdrawal.model.Withdrawal;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class WithdrawalEventMapper {

    public List<WithdrawalEvent> mapWithdrawal(List<Withdrawal> withdrawals) {
        return withdrawals.stream().map(WithdrawalEventMapper::mapWithdrawal).collect(Collectors.toList());
    }

    public List<ScheduledWithdrawalEvent> mapScheduledWithdrawal(List<ScheduledWithdrawal> scheduledWithdrawals) {
        return scheduledWithdrawals.stream().map(WithdrawalEventMapper::mapScheduledWithdrawal).collect(Collectors.toList());
    }

    public WithdrawalEvent mapWithdrawal(Withdrawal withdrawal) {
        return WithdrawalEvent.builder()
                .id(withdrawal.getId())
                .transactionId(withdrawal.getTransactionId())
                .userId(withdrawal.getUserId())
                .withdrawalType(WithdrawalType.IMMEDIATE)
                .status(withdrawal.getStatus())
                .amount(withdrawal.getAmount())
                .createdAt(withdrawal.getCreatedAt())
                .paymentMethodId(withdrawal.getPaymentMethodId())
                .build();
    }

    public ScheduledWithdrawalEvent mapScheduledWithdrawal(ScheduledWithdrawal withdrawal) {
        return ScheduledWithdrawalEvent.builder()
                .id(withdrawal.getId())
                .transactionId(withdrawal.getTransactionId())
                .userId(withdrawal.getUserId())
                .withdrawalType(WithdrawalType.SCHEDULED)
                .status(withdrawal.getStatus())
                .amount(withdrawal.getAmount())
                .createdAt(withdrawal.getCreatedAt())
                .paymentMethodId(withdrawal.getPaymentMethodId())
                .executeAt(withdrawal.getExecuteAt())
                .build();
    }
}
