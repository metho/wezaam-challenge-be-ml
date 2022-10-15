package com.wezaam.withdrawal.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private BigDecimal maxWithdrawalAmount;
    private List<PaymentMethodDto> paymentMethods;
}
