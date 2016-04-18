package com.shedhack.spring.actuator.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Enables some custom actuators and some interceptors
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@Import(ActuatorInterceptorConfiguration.class)
public @interface EnableActuatorsAndInterceptors {
}
