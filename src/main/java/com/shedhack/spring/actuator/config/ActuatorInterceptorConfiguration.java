package com.shedhack.spring.actuator.config;

import com.shedhack.spring.actuator.endpoint.ExceptionsEndpoint;
import com.shedhack.spring.actuator.endpoint.TraceRequestEndpoint;
import com.shedhack.spring.actuator.health.ExceptionHealthCheck;
import com.shedhack.spring.actuator.interceptor.ExceptionInterceptor;
import com.shedhack.spring.actuator.interceptor.ActuatorTraceRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the interceptors and actuators related to exceptions and the trace filter.
 */
@Configuration
public class ActuatorInterceptorConfiguration {

    @Bean
    public ExceptionHealthCheck exceptionHealthCheck() {
        return new ExceptionHealthCheck();
    }

    @Bean
    public ExceptionInterceptor exceptionInterceptor() {
        return new ExceptionInterceptor();
    }

    @Bean
    public ActuatorTraceRequestInterceptor traceRequestHandler() {
        return new ActuatorTraceRequestInterceptor();
    }

    @Bean
    public ExceptionsEndpoint exceptionsEndpoint() {
        return new ExceptionsEndpoint();
    }

    @Bean
    public TraceRequestEndpoint traceRequestEndpoint() {
        return new TraceRequestEndpoint();
    }
}
