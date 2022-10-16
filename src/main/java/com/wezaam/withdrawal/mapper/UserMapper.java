package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.dto.UserDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public List<UserDto> mapUsers(List<User> users) {
        return users.stream().map(UserMapper::mapUser).collect(Collectors.toList());
    }

    public UserDto mapUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .maxWithdrawalAmount(user.getMaxWithdrawalAmount())
                .paymentMethods(PaymentMethodMapper.mapPaymentMethods(user.getPaymentMethods()))
                .build();
    }
}
