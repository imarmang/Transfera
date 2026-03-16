package com.example.transfera.exceptions.customExceptions;

public class GoogleAuthException extends RuntimeException {
  public GoogleAuthException(String message) {
    super(message);
  }
}
