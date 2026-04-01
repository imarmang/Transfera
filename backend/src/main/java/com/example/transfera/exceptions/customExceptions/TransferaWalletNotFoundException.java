package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class TransferaWalletNotFoundException extends RuntimeException {
    public TransferaWalletNotFoundException() {

        super( ErrorMessages.BANK_ACCOUNT_NOT_FOUND.getMessage() );

    }
}
