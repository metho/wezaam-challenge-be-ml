package com.wezaam.withdrawal.events;

import com.wezaam.withdrawal.dto.WithdrawalType;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class WithdrawalEvent {
    private Long id;
    private Long transactionId;
    private BigDecimal amount;
    private Instant createdAt;
    private Instant executeAt;
    private Long userId;
    private Long paymentMethodId;
    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;
    private WithdrawalType withdrawalType;
}