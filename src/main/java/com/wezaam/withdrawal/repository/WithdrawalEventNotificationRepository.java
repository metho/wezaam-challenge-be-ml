package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.WithdrawalEventNotification;
import com.wezaam.withdrawal.model.WithdrawalEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface WithdrawalEventNotificationRepository extends JpaRepository<WithdrawalEventNotification, Long> {

    List<WithdrawalEventNotification> findAllByWithdrawalEventStatusIn(Collection<WithdrawalEventStatus> eventStatuses);

}
