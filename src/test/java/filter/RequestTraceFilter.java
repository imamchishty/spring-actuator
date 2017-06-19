package filter;

import com.shedhack.trace.request.api.constant.HttpHeaderKeysEnum;
import com.shedhack.trace.request.api.constant.Status;
import com.shedhack.trace.request.api.interceptor.TraceRequestInterceptor;
import com.shedhack.trace.request.api.model.DefaultRequestModel;
import com.shedhack.trace.request.api.model.RequestModel;
import com.shedhack.trace.request.api.threadlocal.RequestThreadLocalHelper;
import filter.utility.HttpUtilities;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <pre>
 *  In distributed systems it is difficult to trace the execution paths of multiple services.
 *  This filter will set several header properties and will also provide
 *  an easy to access ThreadLocal {@link RequestThreadLocalHelper} utility class.
 *
 * When constructing this filter you'll need to provide the application name/Id. This
 * is stored in the RequestModel.
 *
 * The filter, when constructed, takes one or more {@link TraceRequestInterceptor} implementations.
 * Each interceptor will be called when the request model is created (i.e. on entry) and also
 * on exit (fulfilment of the request). When the request completes, the response header will also
 * include the 'applicationId', 'spanId' and 'traceId'.
 *
 * All of these will be available the MDC.
 *
 * </pre>
 *
 * @author imamchishty
 */
public class RequestTraceFilter implements Filter {

    /**
     * Default constructor.
     */
    public RequestTraceFilter(String applicationId, List<TraceRequestInterceptor> interceptors) {
        this.appId = applicationId;

        if(interceptors != null) {
            this.interceptors = interceptors;
        }
        else {
            this.interceptors = Collections.EMPTY_LIST;
        }
    }

    public RequestTraceFilter(String applicationId, TraceRequestInterceptor interceptor) {
        this.appId = applicationId;
        this.interceptors = Arrays.asList(interceptor);
    }

    private final String appId;

    private final List<TraceRequestInterceptor> interceptors;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        RequestModel model = null;

        try {

            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Get the traceId
            String traceId = MDC.get(HttpHeaderKeysEnum.TRACE_ID.key());

            // Get the spanId
            String spanId = MDC.get(HttpHeaderKeysEnum.SPAN_ID.key());

            // create the model
            model = build(httpRequest, traceId, spanId);

            // Call the interceptors
            onEntry(model);

            // Set in the thread local for easy access.
            RequestThreadLocalHelper.set(model);

            // Add request-id and group-id to the response header.
            addResponseHeaders((HttpServletResponse) response, model);

            // continue down the chain
            chain.doFilter(httpRequest, response);
        }
        finally {

            // Update the model
            model = update(response, model);

            // Call the interceptors
            onExit(model);

            // clean up
            RequestThreadLocalHelper.clear();
        }
    }

    @Override
    public void destroy() {

    }

    // --------------
    // Helper methods
    // --------------

    /**
     * Adds the request-id and group-id to the response header.
     * @param response response for the client
     * @param model request model formed when the request was first received.
     */
    private void addResponseHeaders(HttpServletResponse response, RequestModel model) {
        response.addHeader(HttpHeaderKeysEnum.SPAN_ID.key(), model.getSpanId());
        response.addHeader(HttpHeaderKeysEnum.TRACE_ID.key(), model.getTraceId());
        response.addHeader(HttpHeaderKeysEnum.APPLICATION_ID.key(), appId);
    }


    // Set the response date/time and also the status - also check for exceptions
    public RequestModel update(ServletResponse servletResponse, RequestModel model) {

        if(model != null) {

            HttpServletResponse response = (HttpServletResponse) servletResponse;

            model.setResponseDateTime(new Date().getTime());
            model.setHttpStatusCode(response.getStatus());

            // Attempt to get the exception Id
            if (responseContainsFailureStatusCode(response.getStatus())) {
                String exceptionId = response.getHeader(HttpHeaderKeysEnum.EXCEPTION_ID.key());

                if (exceptionId != null) {
                    model.setExceptionId(exceptionId);
                    MDC.put(HttpHeaderKeysEnum.EXCEPTION_ID.key(), exceptionId);
                }

                model.setStatus(Status.FAILED);
                MDC.put(HttpHeaderKeysEnum.REQUEST_STATUS.key(), Status.FAILED.name());
            }
            else {
                model.setStatus(Status.COMPLETED);
                MDC.put(HttpHeaderKeysEnum.REQUEST_STATUS.key(), Status.COMPLETED.name());
            }

        }

        return model;
    }

    /**
     * Call all the interceptors passing the request.
     * @param request model formed via the http headers
     */
    private void onEntry(RequestModel request) {
        for(TraceRequestInterceptor interceptor : interceptors) {
            interceptor.onEntry(request);
        }
    }

    /**
     * Call all the interceptors passing the request.
     * @param request model formed via the http headers
     */
    private void onExit(RequestModel request) {
        for(TraceRequestInterceptor interceptor : interceptors) {
            interceptor.onExit(request);
        }
    }

    /**
     * Checks if the status code is in the failure range.
     * @param status from the response object
     * @return true if the status code is 400-500
     */
    public boolean responseContainsFailureStatusCode(int status) {
        return (status >= 400 && status <= 500);
    }

    private RequestModel build(HttpServletRequest httpRequest, String spanId, String traceId) {
        
        return new DefaultRequestModel().builder(appId, spanId, traceId)
                .withRequestDateTime(new Date().getTime())
                .withClientAddress(httpRequest.getRemoteAddr())
                .withHostAddress(httpRequest.getHeader(HttpHeaderKeysEnum.HOST.key()))
                .withPath(httpRequest.getRequestURI())
                .withHttpMethod(httpRequest.getMethod())
                .withSessionId(httpRequest.getSession().getId())
                .withHttpHeaders(HttpUtilities.headerNamesValuesAsString(httpRequest))
                .withStatus(Status.RUNNING).build();
    }

    // ---------------------------------------------------
    // HTTP Header Wrapper - adds the missing HTTP headers
    // ---------------------------------------------------

    public class HeaderWrapper extends HttpServletRequestWrapper {

        public HeaderWrapper(HttpServletRequest request) {
            super(request);
        }

        private Map<String, String> headerMap = new HashMap<>();

        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = super.getHeader(name);
            if (headerMap.containsKey(name)) {
                headerValue = headerMap.get(name);
            }
            return headerValue;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            for (String name : headerMap.keySet()) {
                names.add(name);
            }
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = Collections.list(super.getHeaders(name));
            if (headerMap.containsKey(name)) {
                values.add(headerMap.get(name));
            }
            return Collections.enumeration(values);
        }

    }
}
