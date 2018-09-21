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
package com.ibm.websphere.sample.snatcher;

import java.io.BufferedInputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ibm.websphere.sample.twitter4j.TwitterManager;

import twitter4j.Status;

/**
 * This is a stand-alone Java program that subscribes to Tweets filtered by a set of
 * input strings (which defaults to tweets about the NYC Metro Transit Authority (MTA)).
 * The twitter4j Status object is flattened and written to a file.
 * You must provide an input parameter that points to a properties file containing
 * the tokens required to connect to Twitter.
 * @author Cassandra Newcomer
 * @author David Follis
 */
public class TweetSnatcher {
	
	static private TwitterManager tm;
	static private ConcurrentLinkedQueue<Status> clq = new ConcurrentLinkedQueue<Status>();

	/**
	 * Subscribes to tweets and writes them to a file
	 * @param args The first argument is required and must be the full path to a properties file
	 * containg the required properties to connect to Twitter.  Go to https://developer.twitter.com/en/apply-for-access
	 * to get your own.  The second parameter is optional and can be a set of comma separated strings to
	 * provide to twitter to filter the tweets received.  Defaults to tweets about or by the MTA.
	 * @throws Exception Anything that goes wrong...
	 */
	public static void main(String[] args) throws Exception {

		if (args.length==0) {
			System.out.println("First argument must be the full path of the twitter properties file");
			return;
		}
		
		String twitterPropFile = args[0];
		String inputFilter = null;
		
		if (args.length==2) {
			inputFilter = args[1];
		} else {
			inputFilter = new String("MTA,#MTA,@MTA");			
		}
		
		boolean done = false;
				
		System.out.println("Using twitter properties from: "+twitterPropFile);
		System.out.println("Using tweet filters:  "+inputFilter);
		System.out.println("Press Return/Enter to stop");
		
		String[] filter = inputFilter.split(",");
    	tm = new TwitterManager(twitterPropFile, filter, clq);
       	tm.start();
       	
       	TweetStatusFileWriter tsfw = new TweetStatusFileWriter();
       	
       	BufferedInputStream bis = null;
       	try {
       		bis = (BufferedInputStream) System.in;
       	} catch (ClassCastException exc) {
       		// Some JDK/JREs seem to hit this, since we're just trying to use 'Enter/Return' to stop the program, we can issue a warning message
       		// and continue.
       		System.out.println("Stop on Return/Enter disabled.");
       		System.out.println("Kill with Ctrl+C or kill (or other OS) command.");
       	}
       	
    	Status status = null;
    	while (!done) {
    		synchronized(clq){
        	   clq.wait();
    		}
        	status = clq.poll();
        	tsfw.write(status);
        	
        	if (bis != null && bis.available()!=0) {
        		done = true;
        	}
        	
    	}
    	
    	tsfw.close();

    	tm.stop();
		
	}

}
