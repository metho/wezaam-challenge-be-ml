package com.wezaam.withdrawal.config;

import com.wezaam.withdrawal.exception.EntityNotFoundException;
import com.wezaam.withdrawal.exception.PaymentMethodBelongsToDifferentUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<String> userNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(String.format("Could not find %s by id: %d", ex.getEntityName(), ex.getId()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = PaymentMethodBelongsToDifferentUserException.class)
    public ResponseEntity<String> paymentMethodBelongsToDifferentUserException(PaymentMethodBelongsToDifferentUserException ex, WebRequest request) {
        return new ResponseEntity<>(String.format("Payment method with id: %d belongs to user with id: %d but was " +
                "requested for user with id: %d", ex.getPaymentId(), ex.getActualUserId(), ex.getUserInRequestId()),
                HttpStatus.BAD_REQUEST);
    }

}
