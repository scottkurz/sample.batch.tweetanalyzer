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
package com.ibm.websphere.sample.batch;

import java.util.logging.Logger;

import javax.batch.api.listener.AbstractJobListener;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class SimpleJobListener extends AbstractJobListener {

    private final static Logger logger = Logger.getLogger("sample");

    @Inject JobContext ctx;

    /**
     * @see AbstractJobListener#AbstractJobListener()
     */
    public SimpleJobListener() {
        super();
    }

    /////////////////////////////
    //
    // Log at warning level.  It's nothing bad, just elevate the level 
    // so it filters through to console log.
    //
    /////////////////////////////

    @Override
    public void beforeJob() {
        logger.warning("Job (instance,execution) = (" + ctx.getInstanceId() + "," + ctx.getExecutionId() + ") is beginning execution.");
    }

    @Override
    public void afterJob() {
        logger.warning("Job (instance,execution) = (" + ctx.getInstanceId() + "," + ctx.getExecutionId() + ") is finished execution.");
    }

}
