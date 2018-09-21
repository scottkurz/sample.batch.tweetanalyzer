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
package com.ibm.websphere.sample.twitter4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * The listener class to receive notifications of new tweets via a twitter4j Status object
 * @author Cassandra Newcomer
 * @author David Follis
 */
public class TwitterStatusListener implements StatusListener {

	private static final Logger log = Logger.getLogger( TwitterStatusListener.class.getName() );
	private ConcurrentLinkedQueue<Status> clq;
	
	/**
	 * Constructor to create the listener and save the shared queue used to communicate with whoever
	 * wants these tweets
	 * @param concurrentlinkedQueue The shared queue used to communicate with the consumer of these tweets
	 */
	public TwitterStatusListener(ConcurrentLinkedQueue<Status> concurrentlinkedQueue) {
		clq = concurrentlinkedQueue;
	}
	
	@Override
	public void onException(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg) {
	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		log.log(Level.INFO, "Got stall warning:" + warning);
	}

	@Override
	public void onStatus(Status status) {
		try {			  
		      clq.add(status);     // put the tweet on the queue
		      synchronized(clq) {
		           clq.notify();   // wake up whoever is waiting for these
		      }
		} catch (Exception e) {
			System.out.println("exceptional!");  // oh dear...
			e.printStackTrace();
		}
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		log.log(Level.INFO, "Got track limitation notice:" +
	       numberOfLimitedStatuses);
	}

}
