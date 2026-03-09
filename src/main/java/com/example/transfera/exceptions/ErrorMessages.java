package com.example.transfera.exceptions;

public enum ErrorMessages {

    // CAN ADD TO THIS LIST OVER TIME
    // messages all in one place
    BANK_ACCOUNT_NOT_FOUND( "Bank account not found" ),
    USER_NOT_FOUND( "User not found" ),
    FEATURE_NOT_IMPLEMENTED( "This feature is not yet available" ),
    INVALID_RESET_TOKEN_EXCEPTION( "Invalid or expired reset token" );
//    INSUFFICIENT_FUNDS("Insufficient funds"),
//    ACCOUNT_NUMBER_REQUIRED("Account number is required");


    private final String message;

    ErrorMessages( String message ) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
