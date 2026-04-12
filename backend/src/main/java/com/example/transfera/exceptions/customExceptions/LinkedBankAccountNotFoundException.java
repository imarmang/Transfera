package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class LinkedBankAccountNotFoundException extends RuntimeException {
    public LinkedBankAccountNotFoundException() {
        super( ErrorMessages.LINKED_ACCOUNT_DOES_NOT_EXIST.getMessage() );
    }
}
