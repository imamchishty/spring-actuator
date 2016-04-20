# spring-actuator
Spring actuators, with focus on exception handling and request tracing.

## Configuration

    @EnableActuatorsAndInterceptors
    
## Health Check
    
'/health' now includes the exception count from the exception controller.
    
## Interceptors
    
    exception.interceptor.queue.size (defaults to 50)
    exception.interceptor.endpoint (defaults to exceptions)
    trace.interceptor.queue.size (defaults to 50)
    trace.interceptor.endpoint (defaults to requests)
    exception.interceptor.stacktrace: false
       
## TraceRequestFilter
       
        @Autowired
        private TraceRequestHandler traceRequestHandler;
    
    @Bean
    public FilterRegistrationBean requestIdFilterRegistrationBean() {
        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new RequestTraceFilter(appName, jpaTraceRequestService,
                            Arrays.asList(new DefaultLoggingHandler(), traceRequestHandler)));
        return filter;
    }
           
           
