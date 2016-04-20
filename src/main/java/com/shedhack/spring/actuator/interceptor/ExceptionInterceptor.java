package com.shedhack.spring.actuator.interceptor;

import com.google.common.collect.EvictingQueue;
import com.shedhack.exception.core.ExceptionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 *     Intercepts exceptions and adds them to a ring buffer for an actuator endpoint.
 *     Provides the list (of {@link ExceptionModel} via getList() method.
 *
 *     You can provide the queue size by setting:
 *
 *     exception.interceptor.queue.size
 *
 *     The default is 50.
 *
 *     If you don't want to see the stack trace then set this to false, e.g.
 *
 *     exception.interceptor.stacktrace: false
 * </pre>
 */
@Component
public class ExceptionInterceptor implements com.shedhack.exception.controller.spring.ExceptionInterceptor {

    @Value("${exception.interceptor.queue.size:50}")
    private int queueSize = 50;

    @Value("${exception.interceptor.stacktrace:true}")
    private boolean showTrace;

    private EvictingQueue<ExceptionWrapper> queue;

    public ExceptionInterceptor() {
    }

    @PostConstruct
    private void createQueue() {
        queue = EvictingQueue.create(queueSize);
    }

    public void handle(ExceptionModel exceptionModel, Exception e) {
        if(exceptionModel!=null) {

            if(showTrace) {
                queue.add(new ExceptionWrapper(exceptionModel, e));
            }
            else {
                queue.add(new ExceptionWrapper(exceptionModel, null));
            }
        }
    }

    public List<ExceptionWrapper> getList() {

        List<ExceptionWrapper> models = new ArrayList<>(queueSize);

        Iterator<ExceptionWrapper> iterator = queue.iterator();
        while(iterator.hasNext()){
            models.add(iterator.next());
        }

        return models;
    }

    public void clearQueue() {
        queue.clear();
    }

}
