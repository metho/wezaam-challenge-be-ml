package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.response.UserResponse;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public List<UserResponse> mapToUserResponses(List<User> users) {
        return users.stream().map(UserMapper::mapToUserResponse).collect(Collectors.toList());
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .maxWithdrawalAmount(user.getMaxWithdrawalAmount())
                .paymentMethods(PaymentMethodMapper.mapPaymentMethods(user.getPaymentMethods()))
                .build();
    }
}
