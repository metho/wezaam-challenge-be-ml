package com.wezaam.withdrawal.mapper;


import com.wezaam.withdrawal.dto.PaymentMethodDto;
import com.wezaam.withdrawal.model.PaymentMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentMethodMapperTest {

    @Test
    void shouldMapPaymentMethodEntityToDto() {
        //given
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName("test");

        //when
        PaymentMethodDto paymentMethodDto = PaymentMethodMapper.mapToPaymentMethodDto(paymentMethod);

        //then
        assertEquals(1L, paymentMethodDto.getId());
        assertEquals("test", paymentMethodDto.getName());

    }
}