package com.shedhack.spring.actuator.interceptor;

import com.shedhack.exception.core.ExceptionModel;

/**
 * <pre>Wraps both the exception and the model for the /exceptions actuator</pre>
 */
public class ExceptionWrapper {

    private Exception exception;
    private ExceptionModel model;

    public ExceptionWrapper(){}

    public ExceptionWrapper(ExceptionModel model, Exception exception) {
        this.model = model;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public ExceptionModel getModel() {
        return model;
    }

    public void setModel(ExceptionModel model) {
        this.model = model;
    }
}
