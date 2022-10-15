package com.wezaam.withdrawal.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentMethodResponse {
    private Long id;
    private String name;
}
