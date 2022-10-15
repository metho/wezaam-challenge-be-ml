package com.wezaam.withdrawal.mappers;

import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.response.PaymentMethodResponse;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class PaymentMethodMapper {

    public List<PaymentMethodResponse> mapPaymentMethods(List<PaymentMethod> paymentMethods) {
        return paymentMethods.stream().map(PaymentMethodMapper::mapPaymentMethod).collect(Collectors.toList());
    }

    public PaymentMethodResponse mapPaymentMethod(PaymentMethod paymentMethod) {
        return PaymentMethodResponse.builder()
                .id(paymentMethod.getId())
                .name(paymentMethod.getName())
                .build();
    }
}
