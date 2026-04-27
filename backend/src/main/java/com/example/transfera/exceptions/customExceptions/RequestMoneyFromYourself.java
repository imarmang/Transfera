package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class RequestMoneyFromYourself extends RuntimeException {
    public RequestMoneyFromYourself() {
        super( ErrorMessages.REQUEST_MONEY_FROM_YOURSELF_ERROR.getMessage() );

    }
}
