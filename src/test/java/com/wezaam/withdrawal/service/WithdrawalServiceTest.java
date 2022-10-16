package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import com.wezaam.withdrawal.exception.PaymentMethodBelongsToDifferentUserException;
import com.wezaam.withdrawal.exception.PaymentNotFoundException;
import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.repository.WithdrawalEventNotificationRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.request.WithdrawalRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @InjectMocks
    private WithdrawalService withdrawalService;
    @Mock
    private WithdrawalRepository withdrawalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WithdrawalEventNotificationRepository withdrawalEventNotificationRepository;
    @Mock
    private WithdrawalProcessingService withdrawalProcessingService;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Test
    void createWithdrawalShouldFailIfUserDoesNotExist() {
        //given
        WithdrawalRequest withdrawalRequest = buildWithdrawalRequest();
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        //when && then
        assertThrows(UserNotFoundException.class, () -> withdrawalService.createWithdrawal(withdrawalRequest));
    }

    @Test
    void createWithdrawalShouldFailIfPaymentDoesNotExist() {
        //given
        WithdrawalRequest withdrawalRequest = buildWithdrawalRequest();
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(paymentMethodRepository.findById(5L)).thenReturn(Optional.empty());

        //when && then
        assertThrows(PaymentNotFoundException.class, () -> withdrawalService.createWithdrawal(withdrawalRequest));
    }

    @Test
    void createWithdrawalShouldFailIfPaymentExistsButBelongsToDifferentUser() {
        //given
        WithdrawalRequest withdrawalRequest = buildWithdrawalRequest();
        PaymentMethod paymentMethod = buildPaymentMethod(5L, 2L);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(paymentMethodRepository.findById(5L)).thenReturn(Optional.of(paymentMethod));

        //when && then
        assertThrows(PaymentMethodBelongsToDifferentUserException.class, () -> withdrawalService.createWithdrawal(withdrawalRequest));
    }


    private static PaymentMethod buildPaymentMethod(Long paymentId, Long userId) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(paymentId);
        paymentMethod.setName("testPayment");
        User user = new User();
        user.setId(userId);
        paymentMethod.setUser(user);
        return paymentMethod;
    }

    private static WithdrawalRequest buildWithdrawalRequest() {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUserId(1L);
        withdrawalRequest.setAmount(BigDecimal.TEN);
        withdrawalRequest.setWithdrawalTypeDto(WithdrawalTypeDto.IMMEDIATE);
        withdrawalRequest.setPaymentMethodId(5L);
        return withdrawalRequest;
    }

}