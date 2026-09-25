package com.simple.bank.udemy.exception;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException(String error) {
        super(error);
    }
}
