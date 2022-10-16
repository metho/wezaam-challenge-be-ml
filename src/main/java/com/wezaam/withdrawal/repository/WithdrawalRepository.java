package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.model.WithdrawalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findAllByExecuteAtBeforeAndWithdrawalTypeAndStatus(Instant date, WithdrawalType withdrawalType,
                                                                        WithdrawalStatus status);
}
