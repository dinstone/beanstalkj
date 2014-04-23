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
import java.util.Map;

import com.dinstone.beanstalkj.internal.Connection;

public class StatsOperation extends AbstractOperation<Map<String, String>> {

    public StatsOperation() {
        this.command = "stats";
    }

    public StatsOperation(long jobId) {
        this.command = "stats-job " + jobId;
    }

    public StatsOperation(String tubeName) {
        this.command = "stats-tube " + tubeName;
    }

    @Override
    protected Map<String, String> parseResponse(String line, Connection connection, byte[] data) throws IOException {
        if (line.startsWith("NOT_FOUND")) {
            return null;
        }

        if (line.startsWith("OK")) {
            String[] tmp = line.split("\\s+");
            int length = Integer.parseInt(tmp[1]);
            if (length > 0) {
                return YamlUtil.yaml2Map(charset, readBody(connection, length, data));
            }
        }

        throw new RuntimeException(line);
    }
}
