package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import com.wezaam.withdrawal.exception.PaymentMethodBelongsToDifferentUserException;
import com.wezaam.withdrawal.exception.PaymentNotFoundException;
import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.model.*;
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
import java.time.Instant;
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
        assertThrows(PaymentMethodBelongsToDifferentUserException.class,
                () -> withdrawalService.createWithdrawal(withdrawalRequest));
    }

    @Test
    void createWithdrawalShouldSavaScheduledWithdrawal() {
        //given
        WithdrawalRequest withdrawalRequest = buildWithdrawalRequest(WithdrawalTypeDto.SCHEDULED);
        PaymentMethod paymentMethod = buildPaymentMethod(5L, 1L);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(paymentMethodRepository.findById(5L)).thenReturn(Optional.of(paymentMethod));

        //when
        WithdrawalDto withdrawal = withdrawalService.createWithdrawal(withdrawalRequest);

        //then
        Mockito.verify(withdrawalRepository, Mockito.times(1)).save(any());
        assertEquals(WithdrawalTypeDto.SCHEDULED, withdrawal.getWithdrawalTypeDto());
    }

    @Test
    void createWithdrawalShouldTriggerImmediateWithdrawal() {
        //given
        WithdrawalRequest withdrawalRequest = buildWithdrawalRequest(WithdrawalTypeDto.IMMEDIATE);
        PaymentMethod paymentMethod = buildPaymentMethod(5L, 1L);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(paymentMethodRepository.findById(5L)).thenReturn(Optional.of(paymentMethod));

        //when
        WithdrawalDto withdrawal = withdrawalService.createWithdrawal(withdrawalRequest);

        //then
        Mockito.verify(withdrawalRepository, Mockito.times(1)).save(any());
        assertEquals(WithdrawalTypeDto.IMMEDIATE, withdrawal.getWithdrawalTypeDto());
    }

    @Test
    void processWithdrawalShouldSetTransactionIdAndCorrectStatusOnSuccess() throws TransactionException {
        //given
        Withdrawal withdrawal = buildWithdrawal();
        PaymentMethod paymentMethod = buildPaymentMethod(1L, 5L);
        Mockito.when(withdrawalProcessingService.sendToProcessing(any(), any())).thenReturn(1234L);

        //when
        withdrawalService.processWithdrawal(paymentMethod, withdrawal);

        //then
        assertEquals(WithdrawalStatus.PROCESSING, withdrawal.getStatus());
        assertEquals(1234L, withdrawal.getTransactionId());
        Mockito.verify(withdrawalEventNotificationRepository, Mockito.times(1)).save(any());
        Mockito.verify(withdrawalRepository, Mockito.times(1)).save(any());
    }

    @Test
    void processWithdrawalShouldSetFailedStatusOnTransactionException() throws TransactionException {
        //given
        Withdrawal withdrawal = buildWithdrawal();
        PaymentMethod paymentMethod = buildPaymentMethod(1L, 5L);
        Mockito.when(withdrawalProcessingService.sendToProcessing(any(), any())).thenThrow(TransactionException.class);

        //when
        withdrawalService.processWithdrawal(paymentMethod, withdrawal);

        //then
        assertEquals(WithdrawalStatus.FAILED, withdrawal.getStatus());
        Mockito.verify(withdrawalEventNotificationRepository, Mockito.times(1)).save(any());
        Mockito.verify(withdrawalRepository, Mockito.times(1)).save(any());
    }

    @Test
    void processWithdrawalShouldSetInternalErrorStatusOnUncheckedException() throws TransactionException {
        //given
        Withdrawal withdrawal = buildWithdrawal();
        PaymentMethod paymentMethod = buildPaymentMethod(1L, 5L);
        Mockito.when(withdrawalProcessingService.sendToProcessing(any(), any())).thenThrow(NullPointerException.class);

        //when
        withdrawalService.processWithdrawal(paymentMethod, withdrawal);

        //then
        assertEquals(WithdrawalStatus.INTERNAL_ERROR, withdrawal.getStatus());
        Mockito.verify(withdrawalEventNotificationRepository, Mockito.times(1)).save(any());
        Mockito.verify(withdrawalRepository, Mockito.times(1)).save(any());
    }

    private static Withdrawal buildWithdrawal() {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(1L);
        withdrawal.setAmount(BigDecimal.TEN);
        Instant executeAt = Instant.now();
        Instant createdAt = Instant.now();
        withdrawal.setExecuteAt(executeAt);
        withdrawal.setCreatedAt(createdAt);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);
        withdrawal.setTransactionId(1234L);
        withdrawal.setPaymentMethodId(5L);
        return withdrawal;
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

    private static WithdrawalRequest buildWithdrawalRequest(WithdrawalTypeDto withdrawalTypeDto) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUserId(1L);
        withdrawalRequest.setAmount(BigDecimal.TEN);
        withdrawalRequest.setWithdrawalTypeDto(withdrawalTypeDto);
        withdrawalRequest.setPaymentMethodId(5L);
        return withdrawalRequest;
    }

    private static WithdrawalRequest buildWithdrawalRequest() {
        return buildWithdrawalRequest(WithdrawalTypeDto.IMMEDIATE);
    }

}