package com.wezaam.withdrawal.schedule;

import com.wezaam.withdrawal.event.EventSendResult;
import com.wezaam.withdrawal.mapper.WithdrawalEventMapper;
import com.wezaam.withdrawal.model.WithdrawalEventNotification;
import com.wezaam.withdrawal.model.WithdrawalEventStatus;
import com.wezaam.withdrawal.repository.WithdrawalEventNotificationRepository;
import com.wezaam.withdrawal.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventsScheduler {

    private final EventsService eventsService;
    private final WithdrawalEventNotificationRepository withdrawalEventNotificationRepository;

    @Scheduled(fixedDelay = 5000)
    public void run() {
        List<WithdrawalEventNotification> withdrawalEventNotifications = withdrawalEventNotificationRepository
                .findAllByWithdrawalEventStatusIn(List.of(WithdrawalEventStatus.CREATED, WithdrawalEventStatus.ERROR));

        withdrawalEventNotifications.forEach(notification ->
                eventsService.send(WithdrawalEventMapper.mapWithdrawal(notification.getWithdrawal()))
                        .thenAccept(result -> {
                            if (result == EventSendResult.COMPLETED) {
                                withdrawalEventNotificationRepository.findById(notification.getId())
                                        .ifPresent(eventNotification -> {
                                            eventNotification.setWithdrawalEventStatus(WithdrawalEventStatus.SENT);
                                        });
                            }
                        }));
    }
}
