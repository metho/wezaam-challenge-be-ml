package com.wezaam.withdrawal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "withdrawal_event_notification")
@Getter
@Setter
public class WithdrawalEventNotification {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private WithdrawalEventStatus withdrawalEventStatus;
    private Instant createdAt;
    @OneToOne
    private Withdrawal withdrawal;

}