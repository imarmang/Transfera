package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class LinkedBankAccountExists extends RuntimeException {

    public LinkedBankAccountExists() {
        super( ErrorMessages.LINKED_ACCOUNT_EXISTS.getMessage() );

    }
}
