package com.wezaam.withdrawal.request;

import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
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
    private WithdrawalTypeDto withdrawalTypeDto;
    private Instant executeAt;

    @AssertTrue
    private boolean isExecuteAtSetupForScheduledPayment() {
        return withdrawalTypeDto != WithdrawalTypeDto.SCHEDULED || executeAt != null;
    }
}
