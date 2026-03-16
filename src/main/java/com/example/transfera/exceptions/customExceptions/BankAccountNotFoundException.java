package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException() {

        super( ErrorMessages.BANK_ACCOUNT_NOT_FOUND.getMessage() );

    }
}
