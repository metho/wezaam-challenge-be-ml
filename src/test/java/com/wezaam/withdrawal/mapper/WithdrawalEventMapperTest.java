package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import com.wezaam.withdrawal.event.WithdrawalEvent;
import com.wezaam.withdrawal.dto.WithdrawalStatusDto;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.model.WithdrawalType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WithdrawalEventMapperTest {

    @Test
    void shouldMapWithdrawalEntityToWithdrawalEvent() {
        //given
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(1L);
        withdrawal.setAmount(BigDecimal.TEN);
        Instant executeAt = Instant.now();
        Instant createdAt = Instant.now();
        withdrawal.setExecuteAt(executeAt);
        withdrawal.setCreatedAt(createdAt);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);
        withdrawal.setTransactionId(1234L);
        withdrawal.setPaymentMethodId(5L);

        //when
        WithdrawalEvent withdrawalEvent = WithdrawalEventMapper.mapWithdrawal(withdrawal);

        //then
        assertEquals(1L, withdrawalEvent.getId());
        assertEquals(BigDecimal.TEN, withdrawalEvent.getAmount());
        assertEquals(executeAt, withdrawalEvent.getExecuteAt());
        assertEquals(createdAt, withdrawalEvent.getCreatedAt());
        assertEquals(WithdrawalStatusDto.PENDING, withdrawalEvent.getStatus());
        assertEquals(WithdrawalTypeDto.SCHEDULED, withdrawalEvent.getWithdrawalTypeDto());
        assertEquals(1234L, withdrawalEvent.getTransactionId());
        assertEquals(5L, withdrawalEvent.getPaymentMethodId());

    }
}