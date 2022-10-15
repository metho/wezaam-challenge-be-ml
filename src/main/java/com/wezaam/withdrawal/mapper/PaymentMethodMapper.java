package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.dto.PaymentMethodDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class PaymentMethodMapper {

    public List<PaymentMethodDto> mapPaymentMethods(List<PaymentMethod> paymentMethods) {
        return paymentMethods.stream().map(PaymentMethodMapper::mapPaymentMethod).collect(Collectors.toList());
    }

    public PaymentMethodDto mapPaymentMethod(PaymentMethod paymentMethod) {
        return PaymentMethodDto.builder()
                .id(paymentMethod.getId())
                .name(paymentMethod.getName())
                .build();
    }
}
