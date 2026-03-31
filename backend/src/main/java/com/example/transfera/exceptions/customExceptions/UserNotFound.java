package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class UserNotFound extends RuntimeException {
    public UserNotFound() {
        super( ErrorMessages.USER_NOT_FOUND.getMessage() );
    }
}
