package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EventsService {

    @Async
    public void send(Withdrawal withdrawal) {
        // build and send an event in message queue async
    }

    @Async
    public void send(ScheduledWithdrawal withdrawal) {
        // build and send an event in message queue async
    }
}
