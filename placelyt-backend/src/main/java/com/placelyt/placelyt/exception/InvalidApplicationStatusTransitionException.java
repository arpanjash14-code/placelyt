package com.placelyt.placelyt.exception;

public class InvalidApplicationStatusTransitionException
        extends RuntimeException {

    public InvalidApplicationStatusTransitionException(String message) {
        super(message);
    }
}