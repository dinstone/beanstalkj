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

package com.dinstone.beanstalkj;

import com.dinstone.beanstalkj.internal.Connection;
import com.dinstone.beanstalkj.internal.ConnectionInitializer;
import com.dinstone.beanstalkj.internal.DefaultBeanstalkClient;
import com.dinstone.beanstalkj.internal.NioConnection;
import com.dinstone.beanstalkj.internal.operation.IgnoreOperation;
import com.dinstone.beanstalkj.internal.operation.UseOperation;
import com.dinstone.beanstalkj.internal.operation.WatchOperation;

/**
 * {@link BeanstalkClientFactory} is a factory class, that is responsible for
 * the creation beanstalk client.
 * 
 * @author guojf
 * @version 2.0.0.2013-4-17
 */
public class BeanstalkClientFactory {

    private Configuration config;

    /**
     * factory construction.
     * 
     * @param config
     *        beanstalk client configuration
     */
    public BeanstalkClientFactory(Configuration config) {
        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
        this.config = config;
    }

    /**
     * create a beanstalk client.
     * 
     * @return
     */
    public BeanstalkClient createBeanstalkClient() {
        return new DefaultBeanstalkClient(config);
    }

    /**
     * create a job consumer.
     * 
     * @param watchTubes
     *        the named tube to the watch list for the current connection
     * @return a beanstalk client
     */
    public JobConsumer createJobConsumer(final String... watchTubes) {
        if (watchTubes == null || watchTubes.length == 0) {
            throw new IllegalArgumentException("watchTubes is null");
        }

        final boolean ignoreDefault = config.getBoolean("IgnoreDefaultTube", true);
        ConnectionInitializer initer = new ConnectionInitializer() {

            @Override
            public void initConnection(Connection connection) throws Exception {
                if (watchTubes != null && watchTubes.length > 0) {
                    for (int i = 0; i < watchTubes.length; i++) {
                        connection.handle(new WatchOperation(watchTubes[i]));
                    }
                }

                if (ignoreDefault) {
                    connection.handle(new IgnoreOperation("default"));
                }
            }
        };

        NioConnection connection = new NioConnection(config.getServiceHost(), config.getServicePort());
        return new DefaultBeanstalkClient(config, connection, initer);
    }

    /**
     * create a job producer.
     * 
     * @param useTube
     *        the name of the tube now being used
     * @return a beanstalk client
     */
    public JobProducer createJobProducer(final String useTube) {
        if (useTube == null) {
            throw new IllegalArgumentException("useTube is null");
        }

        ConnectionInitializer initer = new ConnectionInitializer() {

            @Override
            public void initConnection(Connection connection) throws Exception {
                connection.handle(new UseOperation(useTube));
            }
        };

        NioConnection connection = new NioConnection(config.getServiceHost(), config.getServicePort());
        return new DefaultBeanstalkClient(config, connection, initer);
    }
}
