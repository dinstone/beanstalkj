
package com.dinstone.beanstalkj.internal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.beanstalkj.internal.operation.Operation;

public class NioConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(NioConnection.class);

    private SocketChannel channel;

    public NioConnection(String address, int port) {
        try {
            LOGGER.info("connection {} on {}", address, port);

            channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(address, port));
            channel.finishConnect();

            int rsize = channel.socket().getReceiveBufferSize();
            int ssize = channel.socket().getSendBufferSize();
            LOGGER.debug("getReceiveBufferSize {}", rsize);
            LOGGER.debug("getSendBufferSize {}", ssize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T handle(Operation<T> operation) throws IOException {
        operation.writeRequest(this);
        return operation.readRespose(this);
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        while (buf.hasRemaining()) {
            channel.write(buf);
        }
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return channel.read(ByteBuffer.wrap(buffer));
    }
}
