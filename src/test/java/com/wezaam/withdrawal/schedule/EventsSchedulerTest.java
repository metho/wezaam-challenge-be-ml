package com.wezaam.withdrawal.schedule;

import com.wezaam.withdrawal.event.EventSendResult;
import com.wezaam.withdrawal.model.*;
import com.wezaam.withdrawal.repository.WithdrawalEventNotificationRepository;
import com.wezaam.withdrawal.service.EventsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EventsSchedulerTest {

    @InjectMocks
    private EventsScheduler eventsScheduler;

    @Mock
    private EventsService eventsService;

    @Mock
    private WithdrawalEventNotificationRepository withdrawalEventNotificationRepository;

    @Test
    public void shouldCallEventsServiceForEveryWithdrawalEvent() {
        //given
        List<WithdrawalEventNotification> withdrawalEventNotifications = new ArrayList<>();

        WithdrawalEventNotification eventNotification1 = buildEventNotification(Instant.now(), WithdrawalEventStatus.CREATED);

        WithdrawalEventNotification eventNotification2 = buildEventNotification(Instant.now().minus(Period.ofDays(3)), WithdrawalEventStatus.ERROR);

        withdrawalEventNotifications.add(eventNotification1);
        withdrawalEventNotifications.add(eventNotification2);

        Mockito.when(withdrawalEventNotificationRepository.findAllByWithdrawalEventStatusIn(any()))
                .thenReturn(withdrawalEventNotifications);
        Mockito.when(eventsService.send(any()))
                .thenReturn(CompletableFuture.completedFuture(EventSendResult.COMPLETED) );

        //when
        eventsScheduler.run();

        //then
        Mockito.verify(eventsService, Mockito.times(2)).send(any());
        Mockito.verify(withdrawalEventNotificationRepository, Mockito.times(2)).findById(any());
    }

    @Test
    public void shouldNotUpdateNotificationOnSendEventFailure() {
        //given
        List<WithdrawalEventNotification> withdrawalEventNotifications = new ArrayList<>();

        WithdrawalEventNotification eventNotification1 = buildEventNotification(Instant.now(), WithdrawalEventStatus.CREATED);

        WithdrawalEventNotification eventNotification2 = buildEventNotification(Instant.now().minus(Period.ofDays(3)), WithdrawalEventStatus.ERROR);

        withdrawalEventNotifications.add(eventNotification1);
        withdrawalEventNotifications.add(eventNotification2);

        Mockito.when(withdrawalEventNotificationRepository.findAllByWithdrawalEventStatusIn(any()))
                .thenReturn(withdrawalEventNotifications);
        Mockito.when(eventsService.send(any()))
                .thenReturn(CompletableFuture.completedFuture(EventSendResult.ERROR) );

        //when
        eventsScheduler.run();

        //then
        Mockito.verify(eventsService, Mockito.times(2)).send(any());
        Mockito.verify(withdrawalEventNotificationRepository, Mockito.times(0)).findById(any());
    }

    private static WithdrawalEventNotification buildEventNotification(Instant now, WithdrawalEventStatus created) {
        WithdrawalEventNotification eventNotification1 = new WithdrawalEventNotification();
        eventNotification1.setId(1L);
        eventNotification1.setCreatedAt(now);
        eventNotification1.setWithdrawalEventStatus(created);
        Withdrawal withdrawal1 = buildWithdrawal(now);
        eventNotification1.setWithdrawal(withdrawal1);
        return eventNotification1;
    }

    private static Withdrawal buildWithdrawal(Instant createdAt) {
        Withdrawal withdrawal1 = new Withdrawal();
        withdrawal1.setId(1L);
        withdrawal1.setAmount(BigDecimal.TEN);
        Instant executeAt = Instant.now();
        withdrawal1.setExecuteAt(executeAt);
        withdrawal1.setCreatedAt(createdAt);
        withdrawal1.setStatus(WithdrawalStatus.PENDING);
        withdrawal1.setWithdrawalType(WithdrawalType.SCHEDULED);
        withdrawal1.setTransactionId(1234L);
        withdrawal1.setPaymentMethodId(5L);
        return withdrawal1;
    }

}