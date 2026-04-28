package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class UsernameAlreadyTaken extends RuntimeException {
    public UsernameAlreadyTaken() {
        super( ErrorMessages.USERNAME_ALREADY_TAKEN.getMessage() );
    }
}
