/*
 * Copyright (C) 2012~2013 dinstone<dinstone@163.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dinstone.beanstalkj.internal.operation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.dinstone.beanstalkj.internal.Connection;

/**
 * Beanstalk protocol details please refer to the link:
 * <code> https://github.com/kr/beanstalkd/blob/master/doc/protocol.txt
 * </code>
 * 
 * @author guojf
 * @version 1.0.0.2013-4-11
 */
public abstract class AbstractOperation<R> implements Operation<R> {

    /** default is 2^16 */
    public static int maxLength = 64 * 1024;

    private static final byte[] CLF = { '\r', '\n' };

    protected final Charset charset;

    protected String command;

    protected byte[] data;

    public AbstractOperation() {
        this.charset = Charset.forName("ISO-8859-1");
    }

    /**
     * the command to get
     * 
     * @return the command
     * @see AbstractOperation#command
     */
    public String getCommand() {
        return command;
    }

    @Override
    public void writeRequest(Connection connect) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);
        buffer.write(command.getBytes(charset));
        buffer.write(CLF);
        connect.write(buffer.toByteArray());

        if (data != null) {
            connect.write(data);
            connect.write(CLF);
        }
    }

    @Override
    public R readResponse(Connection connect) throws IOException {
        String line = null;
        byte[] dataBuf = null;

        int previous = 0;
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream cmdBuf = new ByteArrayOutputStream(64);
        while (line == null) {
            int count = connect.read(buffer);
            for (int i = 0; i < count; i++) {
                int current = buffer[i];
                if (previous == '\r' && current == '\n') {
                    line = cmdBuf.toString(charset.name()).trim();
                    if (i + 1 < count) {
                        dataBuf = Arrays.copyOfRange(buffer, i + 1, count);
                    }
                    break;
                }
                previous = current;
                cmdBuf.write(current);
            }
        }

        return parseResponse(line, connect, dataBuf);
    }

    protected abstract R parseResponse(String line, Connection connection, byte[] data) throws IOException;

    protected byte[] readBody(Connection connection, int length, byte[] hadBuf) throws IOException {
        byte[] buffer = new byte[length + 2];
        int bufNum = 0;
        if (hadBuf != null) {
            bufNum = hadBuf.length;
            System.arraycopy(hadBuf, 0, buffer, 0, bufNum);
        }

        byte[] readBuf = new byte[length + 2 - bufNum];
        while (bufNum < length + 2) {
            int readNum = connection.read(readBuf);
            System.arraycopy(readBuf, 0, buffer, bufNum, readNum);
            bufNum = +readNum;
        }

        return Arrays.copyOf(buffer, length);
    }
}
