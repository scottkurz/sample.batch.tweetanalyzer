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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

/**
 * Writes a tweet to a file.  A new file is created for tweets within the same minute.
 * As tweets are written, a dot (.) is printed so you can see progress.
 * When a new file is opened, the timestamp is printed.  
 * @author David Follis
 */
public class TweetStatusFileWriter {
	
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd__HH-mm");
    private String ext = ".dat";
    private String stringCurrentDate = null;
    private FileOutputStream fos;
    private PrintWriter pw;

    /**
     * Default constructor
     */
    public TweetStatusFileWriter() {
    	
    }
    
    /**
     * Writes a twitter4j Status object to a file.  One file per minute.
     * @param status  The twitter4j Status object to write
     */
    public void write(Status status) {
    	
    	try {
    		File fileDir = new File("tweets/");
    		if (!fileDir.exists()) {
    			fileDir.mkdir();
    		}

    		Date date = new Date();
    		String stringDate = sdf.format(date);
    		
    		if (stringCurrentDate==null) {
    			// first time here
    			stringCurrentDate = sdf.format(date);
    			System.out.print(stringCurrentDate+" ");
    			fos = new FileOutputStream("tweets/tweetArchive-" + stringDate + ext);
    			pw = new PrintWriter(fos, true);
    		} else if (!(stringDate.equals(stringCurrentDate))) {
    			// new file
    			System.out.print("\n"+stringDate+" ");
    			stringCurrentDate = sdf.format(date);
    			fos.close();
    			pw.close();
    			fos = new FileOutputStream("tweets/tweetArchive-" + stringDate + ext);
    			pw = new PrintWriter(fos, true);
    		}
    		
    		Status quotedStatus = status.getQuotedStatus();
    		Status retweetStatus = status.getRetweetedStatus();
    		
    		//
    		// if quoted, or a retweet use the original tweet instead
    		//
    		// I wonder why a recursive approach wasn't used instead of checking each 
    		// but only to a single-layer of quoting/retweeting.  Maybe because the counts
    		// are included as separate properties?
    		//
    		if (quotedStatus!=null) {
    			status = quotedStatus;
    		}
    		if (retweetStatus!=null) {
    			status = retweetStatus;
 			}    		
    		
    		System.out.print(".");  // So user can see something's happening
    		
    		
    		// Use Adapter to serialize to JSON in a manner specific to this sample.

    		Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
    				.withNullValues(true)
    			    .withAdapters(new StatusAdapter()));
    		
    		pw.println(jsonb.toJson(status,Status.class));

    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
  }

    /**
     * Closes the last open file
     * @throws Exception Anything bad that happens
     */
    public void close() throws Exception {
    	fos.close();
    	pw.close();
    }
	
}
