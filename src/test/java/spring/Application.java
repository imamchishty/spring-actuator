package spring;

import com.google.gson.Gson;
import com.shedhack.exception.controller.spring.ExceptionInterceptor;
import com.shedhack.exception.controller.spring.config.EnableExceptionController;
import com.shedhack.spring.actuator.config.EnableActuatorsAndInterceptors;
import com.shedhack.spring.actuator.interceptor.ActuatorExceptionInterceptor;
import com.shedhack.spring.actuator.interceptor.ActuatorTraceRequestInterceptor;
import filter.DefaultTraceRequestInterceptor;
import filter.RequestTraceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

/**
 * Test Application
 */
@SpringBootApplication
@EnableExceptionController
@EnableActuatorsAndInterceptors
public class Application {

    @Autowired
    private ActuatorTraceRequestInterceptor actuatorTraceRequestInterceptor;

    @Autowired
    private ActuatorExceptionInterceptor actuatorExceptionInterceptor;

    @Bean
    public List<ExceptionInterceptor> exceptionInterceptors() {
        return Arrays.asList(actuatorExceptionInterceptor);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    /**
     * Filter records and logs all HTTP requests.
     * This requires implementation(s) of TraceRequestInterceptors.
     */
    @Bean
    public FilterRegistrationBean requestIdFilterRegistrationBean() {
        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new RequestTraceFilter("appName",
                Arrays.asList(new DefaultTraceRequestInterceptor(gson()), actuatorTraceRequestInterceptor)));
        filter.addUrlPatterns("/*");

        return filter;
    }

    // --------
    // Main
    // --------
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

}
