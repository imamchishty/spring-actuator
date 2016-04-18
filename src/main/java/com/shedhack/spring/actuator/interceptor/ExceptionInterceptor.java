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
 * </pre>
 */
@Component
public class ExceptionInterceptor implements com.shedhack.exception.controller.spring.ExceptionInterceptor {

    @Value("${exception.interceptor.queue.size:50}")
    private int queueSize = 50;

    private EvictingQueue<ExceptionModel> queue;

    public ExceptionInterceptor() {
    }

    @PostConstruct
    private void createQueue() {
        queue = EvictingQueue.create(queueSize);
    }

    public void handle(ExceptionModel exceptionModel, Exception e) {
        if(exceptionModel!=null) {
            queue.add(exceptionModel);
        }
    }


    public List<ExceptionModel> getList() {

        List<ExceptionModel> models = new ArrayList<>(queueSize);

        Iterator<ExceptionModel> iterator = queue.iterator();
        while(iterator.hasNext()){
            models.add(iterator.next());
        }

        return models;
    }

    public void clearQueue() {
        queue.clear();
    }

}
