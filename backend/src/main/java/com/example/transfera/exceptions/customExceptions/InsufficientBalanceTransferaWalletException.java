package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class InsufficientBalanceTransferaWalletException extends RuntimeException {
    public InsufficientBalanceTransferaWalletException() {
        super( ErrorMessages.INSUFFICIENT_BALANCE_TRANSFERA_WALLET.getMessage() );
    }
}