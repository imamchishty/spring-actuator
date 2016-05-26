package com.shedhack.spring.actuator.interceptor;

import com.google.common.collect.EvictingQueue;
import com.shedhack.trace.request.api.interceptor.TraceRequestInterceptor;
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
public class ActuatorTraceRequestInterceptor implements TraceRequestInterceptor {

    @Value("${trace.interceptor.queue.size:50}")
    private int queueSize = 50;

    private EvictingQueue<RequestModel> queue;

    public ActuatorTraceRequestInterceptor() {
    }

    @PostConstruct
    private void createQueue() {
        queue = EvictingQueue.create(queueSize);
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

    public void onEntry(RequestModel requestModel) {
        // only add records on exit
    }

    public void onExit(RequestModel requestModel) {
        queue.add(requestModel);
    }
}
