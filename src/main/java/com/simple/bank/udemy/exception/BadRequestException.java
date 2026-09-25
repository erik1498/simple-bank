package com.simple.bank.udemy.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String error) {
        super(error);
    }
}
