package com.shedhack.spring.actuator.endpoint;

import com.shedhack.spring.actuator.interceptor.ActuatorExceptionInterceptor;
import com.shedhack.spring.actuator.interceptor.ExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 *     Maintains a ring buffer of the last few requests.
 *
 *     The endpoint is available at:
 *
 *     ....../exceptions
 *
 *     This can be changed by setting:
 *
 *     exception.interceptor.endpoint
 *
 * </pre>
 */
@Component
public class ExceptionsEndpoint implements Endpoint<List<ExceptionWrapper>> {

    @Autowired
    private ActuatorExceptionInterceptor interceptor;

    @Value("${exception.interceptor.stacktrace:true}")
    private boolean showTrace;

    @Value("${exception.interceptor.endpoint:exceptions}")
    private String endpoint;

    public String getId() {
        return endpoint;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isSensitive() {
        return true;
    }

    public List<ExceptionWrapper> invoke() {
        return interceptor.getList();
    }
}
