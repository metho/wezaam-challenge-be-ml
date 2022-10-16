package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.dto.UserDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public List<UserDto> mapToUserDtos(List<User> users) {
        return users.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .maxWithdrawalAmount(user.getMaxWithdrawalAmount())
                .paymentMethods(PaymentMethodMapper.mapToPaymentMethodDtos(user.getPaymentMethods()))
                .build();
    }
}
