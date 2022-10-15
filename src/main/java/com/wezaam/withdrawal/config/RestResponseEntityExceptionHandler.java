package com.wezaam.withdrawal.config;

import com.wezaam.withdrawal.errorResponse.NotFoundErrorMessage;
import com.wezaam.withdrawal.exception.EntityNotFoundException;
import com.wezaam.withdrawal.exception.PaymentNotFoundException;
import com.wezaam.withdrawal.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<NotFoundErrorMessage> userNotFoundException(EntityNotFoundException ex, WebRequest request) {
        NotFoundErrorMessage message = new NotFoundErrorMessage(ex.getId(), ex.getEntityName());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

}
