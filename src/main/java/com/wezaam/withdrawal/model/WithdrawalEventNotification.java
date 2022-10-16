package com.wezaam.withdrawal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "withdrawal_event_notification")
@Getter
@Setter
public class WithdrawalEventNotification {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private WithdrawalEventStatus withdrawalEventStatus;
    private Instant createdAt;
    @OneToOne
    private Withdrawal withdrawal;

}