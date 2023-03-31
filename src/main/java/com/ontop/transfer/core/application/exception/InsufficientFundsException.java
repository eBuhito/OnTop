package com.ontop.transfer.core.application.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(long userId){
        super("Insufficient funds for user " + userId);
    }
}
