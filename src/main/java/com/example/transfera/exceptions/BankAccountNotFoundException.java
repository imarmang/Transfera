package com.example.transfera.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException() {

        super( ErrorMessages.BANK_ACCOUNT_NOT_FOUND.getMessage() );

    }
}
