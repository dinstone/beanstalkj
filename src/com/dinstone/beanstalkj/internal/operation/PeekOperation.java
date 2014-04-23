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

import java.io.IOException;

import com.dinstone.beanstalkj.Job;
import com.dinstone.beanstalkj.internal.Connection;

public class PeekOperation extends AbstractOperation<Job> {

    public static enum Type {
        ready, delayed, buried
    }

    public PeekOperation(long jobId) {
        this.command = "peek " + jobId;
    }

    public PeekOperation(Type type) {
        if (type == Type.ready) {
            this.command = "peek-ready";
        } else if (type == Type.delayed) {
            this.command = "peek-delayed";
        } else if (type == Type.buried) {
            this.command = "peek-buried";
        }
    }

    @Override
    protected Job parseResponse(String line, Connection connection, byte[] data) throws IOException {
        if (line.startsWith("NOT_FOUND")) {
            return null;
        }

        if (line.startsWith("FOUND")) {
            String[] tmp = line.split("\\s+");

            Job job = new Job();
            job.setId(Long.parseLong(tmp[1]));

            int length = Integer.parseInt(tmp[2]);
            job.setData(readBody(connection, length, data));

            return job;
        }

        throw new RuntimeException(line);
    }

}
