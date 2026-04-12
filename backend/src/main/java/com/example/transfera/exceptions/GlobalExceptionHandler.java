package com.example.transfera.exceptions;

import com.example.transfera.exceptions.customExceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransferaWalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(TransferaWalletNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ErrorResponse(exception.getMessage() ) );
    }

    @ExceptionHandler( UserNotFound.class )
    public ResponseEntity<ErrorResponse> handleUserNotFoundException( UserNotFound exception ) {
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( new ErrorResponse( exception.getMessage() ) );
    }

    @ExceptionHandler( FeatureNotImplemented.class )
    public ResponseEntity<ErrorResponse> handleFeatureNotImplemented( FeatureNotImplemented exception ) {
        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).body( new ErrorResponse( exception.getMessage() ) );
    }

    @ExceptionHandler( InvalidResetTokenException.class )
    public ResponseEntity<ErrorResponse> handleInvalidResetTokenException( InvalidResetTokenException exception ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( new ErrorResponse( exception.getMessage() ) );
    }

    @ExceptionHandler( GoogleAuthException.class )
    public ResponseEntity<ErrorResponse> handleGoogleAuthException( GoogleAuthException exception ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( new ErrorResponse( exception.getMessage() ) );
    }

    @ExceptionHandler( LinkedBankAccountExists.class )
    public ResponseEntity<ErrorResponse> handleLinkedBankAccountExists( LinkedBankAccountExists exception ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body( new ErrorResponse( exception.getMessage() ) );
    }

    @ExceptionHandler( LinkedBankAccountNotFoundException.class )
    public ResponseEntity<ErrorResponse> handleLinkedBankAccountNotFoundException( LinkedBankAccountNotFoundException exception ) {
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( new ErrorResponse( exception.getMessage() ) );
    }
}
