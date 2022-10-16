package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.event.EventSendResult;
import com.wezaam.withdrawal.event.WithdrawalEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EventsService {

    @Async
    public CompletableFuture<EventSendResult> send(WithdrawalEvent withdrawal) {
        return CompletableFuture.completedFuture(EventSendResult.COMPLETED);
    }

}
