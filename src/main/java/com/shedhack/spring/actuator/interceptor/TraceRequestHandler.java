package com.shedhack.spring.actuator.interceptor;

import com.google.common.collect.EvictingQueue;
import com.shedhack.trace.request.api.logging.LoggingHandler;
import com.shedhack.trace.request.api.model.RequestModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 *     Trace Request interceptor that stores X number of request (as a ring buffer).
 *
 *     You can set the queue size (default is 50):
 *
 *     trace.interceptor.queue.size
 *
 * </pre>
 */
@Component
public class TraceRequestHandler implements LoggingHandler {

    @Value("${trace.interceptor.queue.size:50}")
    private int queueSize = 50;

    private EvictingQueue<RequestModel> queue;

    public TraceRequestHandler() {
    }

    @PostConstruct
    private void createQueue() {
        queue = EvictingQueue.create(queueSize);
    }

    public void log(RequestModel model) {
        if(model!=null) {
            queue.add(model);
        }
    }

    public List<RequestModel> getList() {

        List<RequestModel> models = new ArrayList<>(queueSize);

        Iterator<RequestModel> iterator = queue.iterator();
        while(iterator.hasNext()){
            models.add(iterator.next());
        }

        return models;
    }

    public void clearQueue() {
        queue.clear();
    }
}
