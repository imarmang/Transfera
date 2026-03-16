package com.example.transfera.exceptions;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {

        super(ErrorMessages.INVALID_RESET_TOKEN_EXCEPTION.getMessage());
    }
}
