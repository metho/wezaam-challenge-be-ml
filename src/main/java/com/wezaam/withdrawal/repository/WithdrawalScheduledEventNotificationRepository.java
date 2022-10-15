package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.ScheduledWithdrawalEventNotification;
import com.wezaam.withdrawal.model.WithdrawalEventNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalScheduledEventNotificationRepository extends JpaRepository<ScheduledWithdrawalEventNotification, Long> {

}
