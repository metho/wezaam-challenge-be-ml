package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import com.wezaam.withdrawal.exception.PaymentMethodBelongsToDifferentUserException;
import com.wezaam.withdrawal.exception.PaymentNotFoundException;
import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.mapper.WithdrawalMapper;
import com.wezaam.withdrawal.model.*;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.repository.WithdrawalEventNotificationRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.request.WithdrawalRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;

    private final UserRepository userRepository;

    private final WithdrawalEventNotificationRepository withdrawalEventNotificationRepository;

    private final WithdrawalProcessingService withdrawalProcessingService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public List<WithdrawalDto> findAllWithdrawals() {
        return WithdrawalMapper.mapWithdrawal(withdrawalRepository.findAll());
    }

    public WithdrawalDto createWithdrawal(WithdrawalRequest withdrawalRequest) {
        if (!userRepository.existsById(withdrawalRequest.getUserId())) {
            throw new UserNotFoundException(withdrawalRequest.getUserId());
        }

        long paymentMethodId = withdrawalRequest.getPaymentMethodId();
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentMethodId));

        validatePaymentMethodBelongsToTheUser(withdrawalRequest, paymentMethod);

        Withdrawal withdrawal = buildWithdrawal(withdrawalRequest, paymentMethod.getId());
        if (withdrawalRequest.getWithdrawalTypeDto() == WithdrawalTypeDto.IMMEDIATE) {
            this.create(withdrawal, paymentMethod);
        } else {
            this.schedule(withdrawal);
        }
        return WithdrawalMapper.mapWithdrawal(withdrawal);
    }

    private static void validatePaymentMethodBelongsToTheUser(WithdrawalRequest withdrawalRequest, PaymentMethod paymentMethod) {
        if (withdrawalRequest.getUserId() != paymentMethod.getUser().getId()) {
            throw new PaymentMethodBelongsToDifferentUserException(paymentMethod.getId(), withdrawalRequest.getUserId(),
                    paymentMethod.getUser().getId());
        }
    }

    public void schedule(Withdrawal scheduledWithdrawal) {
        withdrawalRepository.save(scheduledWithdrawal);
    }

    private static Withdrawal buildWithdrawal(WithdrawalRequest withdrawalRequest, long paymentMethodId) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(withdrawalRequest.getUserId());
        withdrawal.setPaymentMethodId(paymentMethodId);
        withdrawal.setAmount(withdrawalRequest.getAmount());
        withdrawal.setCreatedAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setWithdrawalType(WithdrawalMapper.mapWithdrawalTypeDto(withdrawalRequest.getWithdrawalTypeDto()));
        withdrawal.setExecuteAt(withdrawalRequest.getExecuteAt());
        return withdrawal;
    }

    public void create(Withdrawal withdrawal, PaymentMethod paymentMethod) {
        Withdrawal savedWithdrawal = withdrawalRepository.save(withdrawal);
        executorService.submit(() -> {
            processWithdrawal(withdrawal, paymentMethod, savedWithdrawal);
        });
    }

    private void processWithdrawal(Withdrawal withdrawal, PaymentMethod paymentMethod, Withdrawal savedWithdrawal) {
        try {
            var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
            savedWithdrawal.setStatus(WithdrawalStatus.PROCESSING);
            savedWithdrawal.setTransactionId(transactionId);
        } catch (Exception e) {
            if (e instanceof TransactionException) {
                savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
            } else {
                savedWithdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
            }
        }
        WithdrawalEventNotification withdrawalEventNotification = buildWithdrawalEventNotification(savedWithdrawal);
        withdrawalEventNotificationRepository.save(withdrawalEventNotification);
        withdrawalRepository.save(savedWithdrawal);
    }

    private static WithdrawalEventNotification buildWithdrawalEventNotification(Withdrawal withdrawal) {
        WithdrawalEventNotification withdrawalEventNotification = new WithdrawalEventNotification();
        withdrawalEventNotification.setWithdrawal(withdrawal);
        withdrawalEventNotification.setCreatedAt(Instant.now());
        withdrawalEventNotification.setWithdrawalEventStatus(WithdrawalEventStatus.CREATED);
        return withdrawalEventNotification;
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {
        withdrawalRepository.findAllByExecuteAtBeforeAndWithdrawalTypeAndStatus(Instant.now(),
                        WithdrawalType.SCHEDULED, WithdrawalStatus.PENDING).forEach(this::processScheduled);
    }

    private void processScheduled(Withdrawal withdrawal) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(withdrawal.getPaymentMethodId()).orElse(null);
        if (paymentMethod != null) {
            processWithdrawal(withdrawal, paymentMethod, withdrawal);
        } else {
            log.error("Could not find payment method for scheduled withdrawal with id {}. Payment method id: {}",
                    withdrawal.getId(), withdrawal.getPaymentMethodId());
        }
    }

}
