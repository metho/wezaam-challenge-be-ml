package com.wezaam.withdrawal.request;

import com.wezaam.withdrawal.dto.WithdrawalType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class WithdrawalRequest {

    private long userId;
    private long paymentMethodId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private WithdrawalType withdrawalType;
    @NotNull
    private Instant executeAt;
}
