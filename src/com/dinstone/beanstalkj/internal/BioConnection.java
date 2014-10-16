
package com.dinstone.beanstalkj.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.beanstalkj.internal.operation.Operation;

public class BioConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(BioConnection.class);

    private Socket socket;

    private OutputStream output;

    private InputStream input;

    public BioConnection(String address, int port) {
        try {
            LOGGER.debug("connection {} on {}", address, port);
            socket = new Socket(address, port);

            int rsize = socket.getReceiveBufferSize();
            int ssize = socket.getSendBufferSize();
            LOGGER.debug("getReceiveBufferSize {}", rsize);
            LOGGER.debug("getSendBufferSize {}", ssize);

            output = socket.getOutputStream();
            input = socket.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized <T> T handle(Operation<T> operation) throws IOException {
        operation.writeRequest(this);
        return operation.readResponse(this);
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        output.write(bytes);
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return input.read(buffer);
    }
}
