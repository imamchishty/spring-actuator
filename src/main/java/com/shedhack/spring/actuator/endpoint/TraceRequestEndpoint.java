package com.shedhack.spring.actuator.endpoint;

import com.shedhack.spring.actuator.interceptor.TraceRequestHandler;
import com.shedhack.trace.request.api.model.RequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 *     Maintains a ring buffer of the last few requests.
 *     The endpoint is available at:
 *
 *     ....../requests
 *
 *     This can be changed at:
 *
 *     trace.interceptor.endpoint
 *
 * </pre>
 */
@Component
public class TraceRequestEndpoint implements Endpoint<List<RequestModel>> {

    @Autowired
    private TraceRequestHandler traceRequestHandler;

    @Value("${trace.interceptor.endpoint:requests}")
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

    public List<RequestModel> invoke() {
        return traceRequestHandler.getList();
    }
}
