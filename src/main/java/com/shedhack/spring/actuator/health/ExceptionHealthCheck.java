package com.shedhack.spring.actuator.health;

import com.shedhack.exception.controller.spring.ExceptionController;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * <pre>
 *     Adds the exception count as a health indicator
 * </pre>
 */
@Component
public class ExceptionHealthCheck implements HealthIndicator {

    @Override
    public Health health() {

        int errorCode = check();

        // The app is up, but has dealt with X number of exceptions.
        if (errorCode != 0) {
            return Health.up().withDetail("exceptionsCount", errorCode).build();
        }

        return Health.up().build();
    }

    public int check() {

        return ExceptionController.getExceptionCount();
    }
}
