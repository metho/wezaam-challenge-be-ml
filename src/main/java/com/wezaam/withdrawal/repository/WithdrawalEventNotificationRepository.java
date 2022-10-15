package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.WithdrawalEventNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalEventNotificationRepository extends JpaRepository<WithdrawalEventNotification, Long> {

}
