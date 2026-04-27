package com.example.transfera.exceptions;

public enum ErrorMessages {

    // CAN ADD TO THIS LIST OVER TIME
    // messages all in one place
    BANK_ACCOUNT_NOT_FOUND( "Bank account not found" ),
    USER_NOT_FOUND( "User not found" ),
    FEATURE_NOT_IMPLEMENTED( "This feature is not yet available" ),
    INVALID_RESET_TOKEN_EXCEPTION( "Invalid or expired reset token" ),
    GOOGLE_AUTH_FAILED( "Google authentication failed" ),
    LINKED_ACCOUNT_EXISTS( "This bank information is already linked to your account" ),
    LINKED_ACCOUNT_DOES_NOT_EXIST( "The bank account you requested to pull funds from doesn't exist" ),
    INSUFFICIENT_BALANCE_TRANSFERA_WALLET( "Insufficient balance to complete this transaction" ),
    REQUEST_MONEY_FROM_YOURSELF_ERROR( "You cannot request money from yourself." );



    private final String message;

    ErrorMessages( String message ) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
