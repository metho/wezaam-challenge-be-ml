package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.dto.WithdrawalType;
import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import com.wezaam.withdrawal.model.Withdrawal;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class WithdrawalMapper {

    public List<WithdrawalDto> mapWithdrawal(List<Withdrawal> withdrawals) {
        return withdrawals.stream().map(WithdrawalMapper::mapWithdrawal).collect(Collectors.toList());
    }

    public List<WithdrawalDto> mapScheduledWithdrawal(List<ScheduledWithdrawal> scheduledWithdrawals) {
        return scheduledWithdrawals.stream().map(WithdrawalMapper::mapScheduledWithdrawal).collect(Collectors.toList());
    }

    public WithdrawalDto mapWithdrawal(Withdrawal withdrawal) {
        return WithdrawalDto.builder()
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

    public WithdrawalDto mapScheduledWithdrawal(ScheduledWithdrawal withdrawal) {
        return WithdrawalDto.builder()
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
