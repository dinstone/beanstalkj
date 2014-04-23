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
import java.util.Arrays;
import java.util.List;

import com.dinstone.beanstalkj.internal.Connection;

public class ListTubeOperation extends AbstractOperation<List<String>> {

    public static enum Type {
        all, used, watched
    }

    public ListTubeOperation(Type type) {
        if (type == Type.all) {
            this.command = "list-tubes";
        } else if (type == Type.used) {
            this.command = "list-tube-used";
        } else if (type == Type.watched) {
            this.command = "list-tubes-watched";
        }
    }

    @Override
    protected List<String> parseResponse(String line, Connection connection, byte[] data) throws IOException {
        if (line.startsWith("USING")) {
            String[] tmp = line.split("\\s+");
            return Arrays.asList(tmp[1]);
        }

        if (line.startsWith("OK")) {
            String[] tmp = line.split("\\s+");
            int length = Integer.parseInt(tmp[1]);
            if (length > 0) {
                return YamlUtil.yaml2List(charset, readBody(connection, length, data));
            }
        }

        throw new RuntimeException(line);
    }

}
