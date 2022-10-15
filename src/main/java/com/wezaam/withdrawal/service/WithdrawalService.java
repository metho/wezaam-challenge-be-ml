package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.dto.WithdrawalType;
import com.wezaam.withdrawal.exception.PaymentNotFoundException;
import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.mapper.WithdrawalMapper;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.request.WithdrawalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;

    private final UserService userService;
    private final WithdrawalScheduledRepository withdrawalScheduledRepository;
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
        if (!userService.userExists(withdrawalRequest.getUserId())) {
            throw new UserNotFoundException(withdrawalRequest.getUserId());
        }

        if (!paymentMethodRepository.existsById(withdrawalRequest.getPaymentMethodId())) {
            throw new PaymentNotFoundException(withdrawalRequest.getPaymentMethodId());
        }

        if (withdrawalRequest.getWithdrawalType() == WithdrawalType.IMMEDIATE) {
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(withdrawalRequest.getUserId());
            withdrawal.setPaymentMethodId(withdrawalRequest.getPaymentMethodId());
            withdrawal.setAmount(withdrawalRequest.getAmount());
            withdrawal.setCreatedAt(Instant.now());
            withdrawal.setStatus(WithdrawalStatus.PENDING);
            this.create(withdrawal);
            return WithdrawalMapper.mapWithdrawal(withdrawal);
        } else {
            ScheduledWithdrawal scheduledWithdrawal = new ScheduledWithdrawal();
            scheduledWithdrawal.setUserId(withdrawalRequest.getUserId());
            scheduledWithdrawal.setPaymentMethodId(withdrawalRequest.getPaymentMethodId());
            scheduledWithdrawal.setAmount(withdrawalRequest.getAmount());
            scheduledWithdrawal.setCreatedAt(Instant.now());
            scheduledWithdrawal.setExecuteAt(withdrawalRequest.getExecuteAt());
            scheduledWithdrawal.setStatus(WithdrawalStatus.PENDING);
            this.schedule(scheduledWithdrawal);
            return WithdrawalMapper.mapScheduledWithdrawal(scheduledWithdrawal);
        }
    }

    public void create(Withdrawal withdrawal) {
        Withdrawal pendingWithdrawal = withdrawalRepository.save(withdrawal);

        executorService.submit(() -> {
            Optional<Withdrawal> savedWithdrawalOptional = withdrawalRepository.findById(pendingWithdrawal.getId());

            PaymentMethod paymentMethod;
            if (savedWithdrawalOptional.isPresent()) {
                paymentMethod = paymentMethodRepository.findById(savedWithdrawalOptional.get().getPaymentMethodId()).orElse(null);
            } else {
                paymentMethod = null;
            }

            if (savedWithdrawalOptional.isPresent() && paymentMethod != null) {
                Withdrawal savedWithdrawal = savedWithdrawalOptional.get();
                try {
                    var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                    savedWithdrawal.setStatus(WithdrawalStatus.PROCESSING);
                    savedWithdrawal.setTransactionId(transactionId);
                    withdrawalRepository.save(savedWithdrawal);
                    eventsService.send(savedWithdrawal);
                } catch (Exception e) {
                    if (e instanceof TransactionException) {
                        savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
                    } else {
                        savedWithdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                    }
                    withdrawalRepository.save(savedWithdrawal);
                    eventsService.send(savedWithdrawal);
                }
            }
        });
    }

    public void schedule(ScheduledWithdrawal scheduledWithdrawal) {
        withdrawalScheduledRepository.save(scheduledWithdrawal);
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {
        withdrawalScheduledRepository.findAllByExecuteAtBefore(Instant.now())
                .forEach(this::processScheduled);
    }

    private void processScheduled(ScheduledWithdrawal withdrawal) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(withdrawal.getPaymentMethodId()).orElse(null);
        if (paymentMethod != null) {
            try {
                var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                withdrawal.setStatus(WithdrawalStatus.PROCESSING);
                withdrawal.setTransactionId(transactionId);
                withdrawalScheduledRepository.save(withdrawal);
                eventsService.send(withdrawal);
            } catch (Exception e) {
                if (e instanceof TransactionException) {
                    withdrawal.setStatus(WithdrawalStatus.FAILED);
                } else {
                    withdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                }
                withdrawalScheduledRepository.save(withdrawal);
                eventsService.send(withdrawal);
            }
        }
    }

}
