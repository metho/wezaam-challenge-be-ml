package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.UserDto;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void shouldMapUserEntityToDto() {
        //given
        User user = new User();
        user.setId(1L);
        user.setFirstName("testUser");
        user.setMaxWithdrawalAmount(BigDecimal.TEN);
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(2L);
        paymentMethod.setName("testPaymentMethod");
        paymentMethods.add(paymentMethod);
        user.setPaymentMethods(paymentMethods);

        //when
        UserDto userDto = UserMapper.mapToUserDto(user);

        //then
        assertEquals(1L, userDto.getId());
        assertEquals("testUser", userDto.getFirstName());
        assertEquals(BigDecimal.TEN, userDto.getMaxWithdrawalAmount());
        assertEquals(1, userDto.getPaymentMethods().size());
        assertEquals(2L, userDto.getPaymentMethods().get(0).getId());
        assertEquals("testPaymentMethod", userDto.getPaymentMethods().get(0).getName());
    }
}