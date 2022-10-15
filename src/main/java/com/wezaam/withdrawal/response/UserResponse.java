package com.wezaam.withdrawal.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private BigDecimal maxWithdrawalAmount;
    private List<PaymentMethodResponse> paymentMethods;
}
