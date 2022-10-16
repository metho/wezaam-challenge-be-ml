package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.dto.WithdrawalTypeDto;
import com.wezaam.withdrawal.dto.WithdrawalStatusDto;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.model.WithdrawalType;
import com.wezaam.withdrawal.request.WithdrawalRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WithdrawalMapperTest {

    @Test
    void shouldMapWithdrawalEntityToDto() {
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
        WithdrawalDto withdrawalDto = WithdrawalMapper.mapToWithdrawalDtos(withdrawal);

        //then
        assertEquals(1L, withdrawalDto.getId());
        assertEquals(BigDecimal.TEN, withdrawalDto.getAmount());
        assertEquals(executeAt, withdrawalDto.getExecuteAt());
        assertEquals(createdAt, withdrawalDto.getCreatedAt());
        assertEquals(WithdrawalStatusDto.PENDING, withdrawalDto.getStatus());
        assertEquals(WithdrawalTypeDto.SCHEDULED, withdrawalDto.getWithdrawalTypeDto());
        assertEquals(1234L, withdrawalDto.getTransactionId());
        assertEquals(5L, withdrawalDto.getPaymentMethodId());
    }

    @Test
    void shouldMapWithdrawalRequestToEntity() {
        //given
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setAmount(BigDecimal.TEN);
        Instant executeAt = Instant.now();
        withdrawalRequest.setExecuteAt(executeAt);
        withdrawalRequest.setPaymentMethodId(5L);
        withdrawalRequest.setWithdrawalTypeDto(WithdrawalTypeDto.SCHEDULED);

        //when
        Withdrawal withdrawal = WithdrawalMapper.mapToWithdrawal(withdrawalRequest);

        //then
        assertEquals(BigDecimal.TEN, withdrawal.getAmount());
        assertEquals(executeAt, withdrawal.getExecuteAt());
        assertNotNull(withdrawal.getCreatedAt());
        assertEquals(5L, withdrawal.getPaymentMethodId());
        assertEquals(WithdrawalStatus.PENDING, withdrawal.getStatus());
        assertEquals(WithdrawalType.SCHEDULED, withdrawal.getWithdrawalType());
    }

}