package com.wezaam.withdrawal.exception;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(long id) {
        super(id, "user");
    }

}
