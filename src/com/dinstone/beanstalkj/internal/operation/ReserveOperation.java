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

public class ReserveOperation extends AbstractOperation<Job> {

    public ReserveOperation(long timeout) {
        if (timeout > 0) {
            this.command = "reserve-with-timeout " + timeout;
        } else {
            this.command = "reserve";
        }
    }

    @Override
    protected Job parseResponse(String line, Connection connection, byte[] data) throws IOException {
        if (line.startsWith("TIMED_OUT")) {
            return null;
        }

        if (line.startsWith("DEADLINE_SOON")) {
            return null;
        }

        if (line.startsWith("RESERVED")) {
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
