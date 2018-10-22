/*
 * Copyright 2018 International Business Machines Corp.
 * 
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.websphere.sample.startup;

import javax.annotation.PostConstruct;
import javax.annotation.security.RunAs;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.logging.Logger;

@Singleton
@Startup
@RunAs("JOBSTARTER")
public class ControllerBean {
    @PersistenceContext(unitName = "tweet-persister")
    EntityManager entityManager;
    private final static Logger logger = Logger.getLogger("sample");

    @PostConstruct
    public void initialize() {
        logger.warning("\n\nRunning batch job from the ControllerBean startup EJB\n\n");
        TestJobStarter t = new TestJobStarter();
        t.beginJob();
    }
}
