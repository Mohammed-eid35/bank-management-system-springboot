package com.alien.bank.management.system.exception;

public class RefreshTokenNotValidException extends RuntimeException {
    public RefreshTokenNotValidException(String message) {
        super(message);
    }
}
