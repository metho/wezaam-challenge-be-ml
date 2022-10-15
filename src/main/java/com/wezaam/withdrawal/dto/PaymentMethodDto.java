package com.wezaam.withdrawal.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentMethodDto {
    private Long id;
    private String name;
}
