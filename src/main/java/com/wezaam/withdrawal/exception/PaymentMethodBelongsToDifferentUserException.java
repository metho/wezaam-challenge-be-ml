package com.wezaam.withdrawal.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PaymentMethodBelongsToDifferentUserException extends RuntimeException {
    private final long paymentId;
    private final long userInRequestId;
    private final long actualUserId;
}
