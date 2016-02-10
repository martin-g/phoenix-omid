/**
 * Copyright 2011-2016 Yahoo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yahoo.omid.tso;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.yahoo.omid.tso.LeaseManagement.LeaseManagementException;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.yahoo.omid.tso.TSOServer.TSO_HOST_AND_PORT_KEY;

class LeaseManagementModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseManagementModule.class);

    private final TSOServerCommandLineConfig config;

    LeaseManagementModule(TSOServerCommandLineConfig config) {
        this.config = config;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    LeaseManagement provideLeaseManager(@Named(TSO_HOST_AND_PORT_KEY) String tsoHostAndPort,
                                        TSOChannelHandler tsoChannelHandler,
                                        TSOStateManager stateManager,
                                        CuratorFramework zkClient,
                                        Panicker panicker)
            throws LeaseManagementException {

        if (config.shouldHostAndPortBePublishedInZK) {
            LOG.info("Connection to ZK cluster [{}]", zkClient.getState());
            return new LeaseManager(tsoHostAndPort,
                    tsoChannelHandler,
                    stateManager,
                    config.getLeasePeriodInMs(),
                    zkClient,
                    panicker);
        } else {
            return new NonHALeaseManager(tsoChannelHandler, stateManager);
        }

    }

}
