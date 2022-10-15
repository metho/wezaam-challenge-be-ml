package com.wezaam.withdrawal.mapper;

import com.wezaam.withdrawal.dto.WithdrawalDto;
import com.wezaam.withdrawal.model.ScheduledWithdrawal;
import com.wezaam.withdrawal.model.Withdrawal;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class WithdrawalMapper {

    public List<WithdrawalDto> mapWithdrawal(List<Withdrawal> all) {
        return new ArrayList<>();
    }

    public List<WithdrawalDto> mapScheduledWithdrawal(List<ScheduledWithdrawal> all) {
        return new ArrayList<>();
    }
}
