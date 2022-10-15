package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WithdrawalScheduledRepository extends JpaRepository<ScheduledWithdrawal, Long> {

    List<ScheduledWithdrawal> findAllByExecuteAtBefore(Instant date);
}
