package com.portfolio.expensetracker.common;

/**
 * Thrown when registration is attempted with an email that already exists.
 */
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email is already registered: " + email);
    }
}
