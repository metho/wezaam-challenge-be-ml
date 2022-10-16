package com.wezaam.withdrawal.dto;

import com.wezaam.withdrawal.model.WithdrawalStatus;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class WithdrawalDto {
    private Long id;
    private Long transactionId;
    private BigDecimal amount;
    private Instant createdAt;
    private Instant executeAt;
    private Long userId;
    private Long paymentMethodId;
    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;
    private WithdrawalTypeDto withdrawalTypeDto;
}
