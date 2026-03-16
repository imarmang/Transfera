package com.example.transfera.exceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound() {
        super( ErrorMessages.USER_NOT_FOUND.getMessage() );
    }
}
