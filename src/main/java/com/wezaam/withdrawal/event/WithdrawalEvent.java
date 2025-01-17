package com.wezaam.withdrawal.event;

import com.wezaam.withdrawal.dto.WithdrawalStatusDto;
import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
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
    private WithdrawalStatusDto status;
    private WithdrawalTypeDto withdrawalTypeDto;
}