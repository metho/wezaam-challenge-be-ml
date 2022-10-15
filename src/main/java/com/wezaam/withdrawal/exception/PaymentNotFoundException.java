package com.wezaam.withdrawal.exception;

public class PaymentNotFoundException extends EntityNotFoundException {
    public PaymentNotFoundException(long id) {
        super(id, "payment");
    }
}
