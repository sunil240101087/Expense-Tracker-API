package com.portfolio.expensetracker.security;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * Convenience annotation that injects the authenticated {@link AppUserDetails}
 * into a controller method parameter and hides it from the OpenAPI schema.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal
@Parameter(hidden = true)
@Hidden
public @interface CurrentUser {
}
