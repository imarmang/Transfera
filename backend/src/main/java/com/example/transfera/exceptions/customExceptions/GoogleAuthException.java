package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class GoogleAuthException extends RuntimeException {
    public GoogleAuthException() {
        super(ErrorMessages.GOOGLE_AUTH_FAILED.getMessage() );
    }
}
