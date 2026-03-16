package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {

        super(ErrorMessages.INVALID_RESET_TOKEN_EXCEPTION.getMessage());
    }
}
