package com.portfolio.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Expense Tracker API.
 *
 * <p>A secure, multi-user expense tracking service with JWT authentication,
 * role-based access control, and spending analytics.
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}
