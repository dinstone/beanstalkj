
package com.dinstone.beanstalkj.internal;

import java.io.IOException;

import com.dinstone.beanstalkj.internal.operation.Operation;

public interface Connection {

    void write(byte[] bytes) throws IOException;

    int read(byte[] buffer) throws IOException;

    public <T> T handle(Operation<T> operation) throws IOException;

    void close() throws IOException;
}
