package com.wezaam.withdrawal.errorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotFoundErrorMessage {
    private Long id;
    private String entity;

    @Override
    public String toString() {
        return "Could not find " + entity + "by id: " + id;
    }
}
