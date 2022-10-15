package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.dto.WithdrawalType;
import com.wezaam.withdrawal.exception.PaymentNotFoundException;
import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.mapper.WithdrawalEventMapper;
import com.wezaam.withdrawal.mapper.WithdrawalMapper;
import com.wezaam.withdrawal.model.*;
import com.wezaam.withdrawal.repository.*;
import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.request.WithdrawalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;

    private final UserRepository userRepository;
    private final WithdrawalScheduledRepository withdrawalScheduledRepository;

    private final WithdrawalEventNotificationRepository withdrawalEventNotificationRepository;

    private final WithdrawalScheduledEventNotificationRepository withdrawalScheduledEventNotificationRepository;
    private final WithdrawalProcessingService withdrawalProcessingService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final EventsService eventsService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public List<WithdrawalDto> findAllWithdrawals() {
        List<WithdrawalDto> immediateWithdrawals = WithdrawalMapper.mapWithdrawal(withdrawalRepository.findAll());
        List<WithdrawalDto> scheduledWithdrawals = WithdrawalMapper.mapScheduledWithdrawal(withdrawalScheduledRepository.findAll());
        return Stream.concat(immediateWithdrawals.stream(), scheduledWithdrawals.stream()).collect(Collectors.toList());
    }

    public WithdrawalDto createWithdrawal(WithdrawalRequest withdrawalRequest) {
        if (!userRepository.existsById(withdrawalRequest.getUserId())) {
            throw new UserNotFoundException(withdrawalRequest.getUserId());
        }

        long paymentMethodId = withdrawalRequest.getPaymentMethodId();
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentMethodId));

        if (withdrawalRequest.getWithdrawalType() == WithdrawalType.IMMEDIATE) {
            Withdrawal withdrawal = buildWithdrawal(withdrawalRequest, paymentMethodId);
            this.create(withdrawal, paymentMethod);
            return WithdrawalMapper.mapWithdrawal(withdrawal);
        } else {
            ScheduledWithdrawal scheduledWithdrawal = buildScheduledWithdrawal(withdrawalRequest, paymentMethodId);
            this.schedule(scheduledWithdrawal);
            return WithdrawalMapper.mapScheduledWithdrawal(scheduledWithdrawal);
        }
    }

    private static Withdrawal buildWithdrawal(WithdrawalRequest withdrawalRequest, long paymentMethodId) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(withdrawalRequest.getUserId());
        withdrawal.setPaymentMethodId(paymentMethodId);
        withdrawal.setAmount(withdrawalRequest.getAmount());
        withdrawal.setCreatedAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        return withdrawal;
    }

    private static ScheduledWithdrawal buildScheduledWithdrawal(WithdrawalRequest withdrawalRequest, long paymentMethodId) {
        ScheduledWithdrawal scheduledWithdrawal = new ScheduledWithdrawal();
        scheduledWithdrawal.setUserId(withdrawalRequest.getUserId());
        scheduledWithdrawal.setPaymentMethodId(paymentMethodId);
        scheduledWithdrawal.setAmount(withdrawalRequest.getAmount());
        scheduledWithdrawal.setCreatedAt(Instant.now());
        scheduledWithdrawal.setExecuteAt(withdrawalRequest.getExecuteAt());
        scheduledWithdrawal.setStatus(WithdrawalStatus.PENDING);
        return scheduledWithdrawal;
    }

    @Transactional
    public void create(Withdrawal withdrawal, PaymentMethod paymentMethod) {
        Withdrawal savedWithdrawal = withdrawalRepository.save(withdrawal);

        executorService.submit(() -> {

            try {
                var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                savedWithdrawal.setStatus(WithdrawalStatus.PROCESSING);
                savedWithdrawal.setTransactionId(transactionId);
                withdrawalRepository.save(savedWithdrawal);

                WithdrawalEventNotification withdrawalEventNotification = buildWithdrawalEventState(savedWithdrawal);
                withdrawalEventNotificationRepository.save(withdrawalEventNotification);

//                eventsService.send(WithdrawalEventMapper.mapWithdrawal(savedWithdrawal));

            } catch (Exception e) {
                if (e instanceof TransactionException) {
                    savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
                } else {
                    savedWithdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                }
                withdrawalRepository.save(savedWithdrawal);
                WithdrawalEventNotification withdrawalEventNotification = buildWithdrawalEventState(savedWithdrawal);
                withdrawalEventNotificationRepository.save(withdrawalEventNotification);

//                eventsService.send(WithdrawalEventMapper.mapWithdrawal(savedWithdrawal));
            }

        });
    }

    private static WithdrawalEventNotification buildWithdrawalEventState(Withdrawal withdrawal) {
        WithdrawalEventNotification withdrawalEventNotification = new WithdrawalEventNotification();
        withdrawalEventNotification.setWithdrawal(withdrawal);
        withdrawalEventNotification.setCreatedAt(Instant.now());
        withdrawalEventNotification.setWithdrawalEventStatus(WithdrawalEventStatus.CREATED);
        return withdrawalEventNotification;
    }

    private static ScheduledWithdrawalEventNotification buildScheduledWithdrawalEventState(ScheduledWithdrawal withdrawal) {
        ScheduledWithdrawalEventNotification withdrawalEventNotification = new ScheduledWithdrawalEventNotification();
        withdrawalEventNotification.setScheduledWithdrawal(withdrawal);
        withdrawalEventNotification.setCreatedAt(Instant.now());
        withdrawalEventNotification.setWithdrawalEventStatus(WithdrawalEventStatus.CREATED);
        return withdrawalEventNotification;
    }

    public void schedule(ScheduledWithdrawal scheduledWithdrawal) {
        withdrawalScheduledRepository.save(scheduledWithdrawal);
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {
        withdrawalScheduledRepository.findAllByExecuteAtBefore(Instant.now()).forEach(this::processScheduled);
    }

    private void processScheduled(ScheduledWithdrawal withdrawal) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(withdrawal.getPaymentMethodId()).orElse(null);
        if (paymentMethod != null) {
            try {
                var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                withdrawal.setStatus(WithdrawalStatus.PROCESSING);
                withdrawal.setTransactionId(transactionId);
                withdrawalScheduledRepository.save(withdrawal);
                eventsService.send(WithdrawalEventMapper.mapScheduledWithdrawal(withdrawal));
            } catch (Exception e) {
                if (e instanceof TransactionException) {
                    withdrawal.setStatus(WithdrawalStatus.FAILED);
                } else {
                    withdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                }
                withdrawalScheduledRepository.save(withdrawal);
                ScheduledWithdrawalEventNotification scheduledWithdrawalEventNotification = buildScheduledWithdrawalEventState(withdrawal);
                withdrawalScheduledEventNotificationRepository.save(scheduledWithdrawalEventNotification);
                eventsService.send(WithdrawalEventMapper.mapScheduledWithdrawal(withdrawal));
            }
        }
    }

}
