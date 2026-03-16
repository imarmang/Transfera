package com.example.transfera.exceptions.customExceptions;

import com.example.transfera.exceptions.ErrorMessages;

public class FeatureNotImplemented extends RuntimeException {
    public FeatureNotImplemented() {
        super(ErrorMessages.FEATURE_NOT_IMPLEMENTED.getMessage())
        ;
    }
}
