package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class ProfileAlreadyExists extends RuntimeException {
    public ProfileAlreadyExists() {
        super( ErrorMessages.PROFILE_ALREADY_EXISTS.getMessage() );
    }
}
