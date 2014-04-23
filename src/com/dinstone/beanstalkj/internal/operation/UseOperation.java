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

public class UseOperation extends AbstractOperation<Boolean> {

    public UseOperation(String tube) {
        this.command = "use " + tube;
    }

    @Override
    protected Boolean parseResponse(String cmd, Connection connection, byte[] data) {
        if (cmd.startsWith("USING")) {
            return true;
        }

        throw new IllegalStateException(cmd);
    }
}