package com.alien.bank.management.system.exception;

public class LowBalanceException extends RuntimeException {
    public LowBalanceException() {
        super();
    }

    public LowBalanceException(String message) {
        super(message);
    }
}

