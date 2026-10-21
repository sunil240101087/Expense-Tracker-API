package com.portfolio.expensetracker.config;

import com.portfolio.expensetracker.expense.Category;
import com.portfolio.expensetracker.expense.Expense;
import com.portfolio.expensetracker.expense.ExpenseRepository;
import com.portfolio.expensetracker.user.Role;
import com.portfolio.expensetracker.user.User;
import com.portfolio.expensetracker.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds a demo admin account and a few sample expenses on first startup so the
 * API is explorable immediately. Disabled under the "test" profile.
 */
@Configuration
@Profile("!test")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository userRepository,
                           ExpenseRepository expenseRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.seed.admin-email}") String adminEmail,
                           @Value("${app.seed.admin-password}") String adminPassword) {
        return args -> {
            if (userRepository.existsByEmail(adminEmail)) {
                return;
            }

            User admin = new User(adminEmail, passwordEncoder.encode(adminPassword), "Demo Admin");
            admin.addRole(Role.ROLE_ADMIN);
            userRepository.save(admin);

            expenseRepository.save(new Expense(admin, "Groceries",
                    new BigDecimal("54.20"), Category.FOOD, LocalDate.now().minusDays(2)));
            expenseRepository.save(new Expense(admin, "Monthly transit pass",
                    new BigDecimal("75.00"), Category.TRANSPORT, LocalDate.now().minusDays(5)));
            expenseRepository.save(new Expense(admin, "Streaming subscription",
                    new BigDecimal("12.99"), Category.ENTERTAINMENT, LocalDate.now().minusDays(10)));
        };
    }
}
