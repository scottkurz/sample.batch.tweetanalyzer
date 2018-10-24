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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.PropertyConfiguration;

/**
 * This class wraps the code needed to interact with Twitter
 * @author Cassandra Newcomer
 * @author David Follis
 */
public class TwitterManager {

	private String propertiesFile;
	private String[] filterString;
	private ConcurrentLinkedQueue<Status> clq;
	
	private Properties props = new Properties();
	private TwitterStream twitterStream;
	
	/**
	 * Constructor that gets the stuff needed to connect to twitter, plus establish a shared queue
	 * to communicate tweets (Status objects) with the subscriber (caller of this class).
	 * @param propFile A properties file containing the authorization tokens to connect to Twitter
	 * @param filter An array of Strings used to filter tweets (i.e. "MTA","#MTA" or,
	 * if you want some serious volume, "realDonaldTrump".  
	 * @param queue The ConcurrentLinkedQueue used to communicate with the thread processing the tweets
	 * @throws Exception Anything that goes wrong, probably problems with the properties file
	 */
	public TwitterManager(String propFile, String[] filter, ConcurrentLinkedQueue<Status> queue) throws Exception {
		propertiesFile=propFile;
		filterString = filter;
		clq = queue;
		InputStream input = new FileInputStream(propertiesFile);
		props.load(input);
	}
	
	/**
	 * Fires up the Tweet Status Listener and gives it the shared queue to put tweet Status objects in.
	 * Note that we filter location roughly around New York City.  
	 */
	public void start() {

		double NYC_LAT_SOUTH = 39.3682;
		double NYC_LONG_WEST = -75.9374;
		double NYC_LAT_NORTH = 42.0329;
		double NYC_LONG_EAST = -71.7187;

    	PropertyConfiguration pc = new PropertyConfiguration(props);
    	TwitterStreamFactory tsf = new TwitterStreamFactory(pc);
    	twitterStream = tsf.getInstance();
        StatusListener sl = new TwitterStatusListener(clq);
        twitterStream.addListener(sl);
        FilterQuery tweetFilterQuery = new FilterQuery();
        tweetFilterQuery.track(filterString)
                .locations(new double[][] { new double[] {NYC_LAT_SOUTH, NYC_LONG_WEST}, new double[] {NYC_LAT_NORTH, NYC_LONG_EAST}})
                .language(new String[] { "en" });
        twitterStream.filter(tweetFilterQuery);

	}
	
	/**
	 * Shuts down the tweet listener (which tears down the threads it creates).
	 * That might be important in a server environment where the server can stay up
	 * past the end of the user of this class and those threads might hang around forever...
	 */
	public void stop() {
    	twitterStream.cleanUp();
    	twitterStream.shutdown();
	}
	
}
