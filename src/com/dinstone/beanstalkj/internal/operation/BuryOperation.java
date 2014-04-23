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

import com.dinstone.beanstalkj.internal.Connection;

public class BuryOperation extends AbstractOperation<Boolean> {

    public BuryOperation(long id, int priority) {
        this.command = "bury " + id + " " + priority;
    }

    @Override
    protected Boolean parseResponse(String line, Connection connection, byte[] data) {
        if (line.startsWith("BURIED")) {
            return true;
        } else if (line.startsWith("NOT_FOUND")) {
            return false;
        }

        throw new RuntimeException(line);
    }

}
