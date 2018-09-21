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

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.batch.api.chunk.ItemWriter;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class implements a JSR-352 ItemWriter.  It receives a list of objects from an
 * ItemProcessor and writes them into a database using JPA.
 * @author Cassandra Newcomer
 */
@Dependent
public class TweetObjectJPAWriter implements ItemWriter {

	private static final Logger log = Logger.getLogger( TweetObjectJPAWriter.class.getName() );
	
    @PersistenceContext(unitName = "tweet-persister")
    EntityManager entityManager;

	
    /**
     * Default constructor. 
     */
    public TweetObjectJPAWriter() {
    }

	/**
     * @see ItemWriter#open(Serializable)
     */
    public void open(Serializable arg0) {
    }

	/**
     * @see ItemWriter#close()
     */
    public void close() {
    }

	/**
     * Write a list of objects into a database using JPA
     */
	public void writeItems(List<java.lang.Object> arg0) {
        try {

        	log.log(Level.INFO, "Writing items");
        	
        	// Loop through all the items
            for (int i = 0; i < arg0.size(); i++) {

                TweetObject tw = (TweetObject) arg0.get(i);

                log.log(Level.INFO, "writing tweet "+tw.getTextContent());
                
                persistTweet(tw);
            }
        } catch (Exception e) {
        	log.log(Level.INFO, "Something went wrong : " + e);
        }
    }

	/**
     * @see ItemWriter#checkpointInfo()
     */
    public Serializable checkpointInfo() {
			return null;
    }

    /**
     * Persist a tweet into the database
     * @param newTweet The TweetObject containing information about the tweet
     */
    public void persistTweet(TweetObject newTweet) {
        try {
        	// Is this tweet already in the database?  
            boolean alreadyExists = entityManager
            .createQuery("SELECT f.statusId FROM TweetObject f WHERE f.statusId = " +
            newTweet.getStatusId())
            .setMaxResults(1).getResultList().isEmpty() ? false : true;
            
        	// Skip if a duplicate in the database, but update the retweet/favorite counts
            if (alreadyExists) {

            TweetObject loadedTweet = (TweetObject) entityManager
                    .createQuery("SELECT t FROM TweetObject t WHERE t.statusId = " + newTweet.getStatusId())
                    .getSingleResult();

                Long oldFavs = loadedTweet.getFavoriteCount();
                Long newFavs = newTweet.getFavoriteCount();
                Long oldRetweets = loadedTweet.getRetweetCount();
                Long newRetweets = newTweet.getRetweetCount();
                loadedTweet.setFavoriteCount(newFavs > oldFavs ? newFavs : oldFavs);
                loadedTweet.setFavoriteCount(newRetweets > oldRetweets ? newRetweets : oldRetweets);
                
                entityManager.merge(loadedTweet);
            } else {
            	// New tweet
                entityManager.persist(newTweet);
            }
        } catch (Exception e) {
            log.log(Level.INFO, "Something went wrong persisting the tweets. Caught exception " + e);
        }
                
    }
    
    
}
