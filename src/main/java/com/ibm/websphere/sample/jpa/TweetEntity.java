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
package com.ibm.websphere.sample.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.ibm.websphere.sample.watson.SentimentObject;

/**
 * This is the entity object used with JPA to persist information about a single tweet
 * @author Cassandra Newcomer
 */
@Entity
@Table(name = "TWEETS", schema = "TWITTERCOLLECTION")
public class TweetEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DBID")
    private long dbId;

    @Column(name = "STATUS_ID")
    private long statusId;

    @Column(name = "ACCOUNT_ID")
    private long accountId;

    @Column(name = "SCREEN_NAME")
    String screenName;

    @Column(name = "REAL_NAME")
    String realName;

    @Column(name = "NUMBER_OF_FOLLOWERS")
    long numberOfFollowers;

    @Lob
    @Column(name = "TEXTCONTENT")
    private String textContent;

    @Column(name = "CREATION_DATE")
    Date creationDate;

    @Column(name = "FAVORITE_COUNT")
    long favoriteCount;

    @Column(name = "RETWEET_COUNT")
    long retweetCount;

    @Column(name = "POPULARITY")
    long popularity;

    @Column(name = "IS_RETWEET")
    boolean isRetweet;

    @Column(name = "QUOTED_AUTHOR_SCREEN_NAME")
    String quotedAuthorScreenName;

    @Column(name = "QUOTED_AUTHOR_REAL_NAME")
    String quotedAuthorRealName;

    @Column(name = "SENTIMENT")
    SentimentObject sentiment;

    @Column(name = "ACCOUNT_LOCATION")
    String accountLocation;

    /**
     * Default constructor.  Sets string values to empty strings so users don't have to worry about getting nulls back.
     * Sets up a neutral SentimentObject by default also.
     * Everything else gets the Java defaults of zero/false.
     */
    public TweetEntity() {
        this.screenName = "";
        this.realName = "";
        this.quotedAuthorRealName = "";
        this.quotedAuthorScreenName = "";
        this.textContent = "";
        this.sentiment = new SentimentObject();
    }

    /**
     * Sets a location of the user tweeting the tweet
     * @param location Where the user was when they tweeted this tweet
     */
    public void setAccountLocation(String location) {
        this.accountLocation = location;
    }

    /**
     * Returns the location of the user who tweeted
     * @return The twitter user's location
     */
    public String getAccountLocation() {
        return this.accountLocation;
    }

    /**
     * Sets a SentimentObject associated with this tweet
     * @param sentiment A SentimentObject
     */
    public void setSentimentObject(SentimentObject sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * Returns the SentimentObject for this tweet
     * @return A SentimentObject for this tweet
     */
    public SentimentObject getSentiment() {
        return this.sentiment;
    }

    /**
     * Returns the Twitter unique id for this particular tweet
     * @return an identifier for the tweet
     */
    public long getStatusId() {
        return this.statusId;
    }

    /**
     * Sets the id for this particular tweet
     * @param id The Twitter unique id for this particular tweet
     */
    public void setStatusId(long id) {
        this.statusId = id;
    }

    /**
     * The numeric ID of the twitter user who tweeted
     * @return The numeric ID of the twitter user who tweeted
     */
    public long getAccountId() {
        return this.accountId;
    }

    /**
     * Sets the numeric twitter ID for the tweeter who tweeted this tweet
     * @param id The numeric id for the user
     */
    public void setAccountId(long id) {
        this.accountId = id;
    }

    /**
     * The tweet itself
     * @return The tweet itself
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * Sets the text for this tweet
     * @param textContent The actual text string of the tweet
     */
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    /**
     * Sets the date the tweet was tweeted
     * @param date the creation Date object for when the tweet was tweeted
     */
    public void setDate(Date date) {
        this.creationDate = date;
    }

    /**
     * Gets the Date object for when the tweet was tweeted
     * @return The Date object when the tweet was created
     */
    public Date getDate() {
        return this.creationDate;
    }

    /**
     * Sets the count of followers for the user who tweeted
     * @param numberOfFollowers the number of followers this user has when they tweeted
     */
    public void setNumberOfFollowers(long numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    /**
     * How many followers did this user have when they tweeted this
     * @return The number of followers
     */
    public long getNumberOfFollowers() {
        return this.numberOfFollowers;
    }

    /**
     * Is this tweet a retweet of some other tweet
     * @return true if it is, false if it isn't
     */
    public boolean checkIfRetweet() {
        return this.isRetweet;
    }

    /**
     * Sets the retweet status
     * @param retweetStatus true if it is a retweet, false if not
     */
    public void setRetweetStatus(boolean retweetStatus) {
        this.isRetweet = retweetStatus;
    }

    /**
     * How popular is this tweet?  Total of favorites and retweets
     * @return A popularity measure for this tweet
     */
    public long getPopularity() {
        return this.popularity;
    }

    /**
     * An internal method used to set the popularity whenever it needs it
     */
    private void setPopularity() {
        this.popularity = this.favoriteCount + this.retweetCount;
    }

    /**
     * Sets a new count of the favorites for this tweet
     * Updates popularity
     * @param favoriteCount The new favorite count for this tweet
     */
    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
        setPopularity();
    }

    /**
     * The current count of favorites for this tweet
     * @return The favorite count for this tweet
     */
    public long getFavoriteCount() {
        return this.favoriteCount;
    }

    /**
     * Sets a new count of the retweets for this tweet
     * Updates popularity
     * @param retweetCount The new retweet count for this tweet
     */
    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
        setPopularity();
    }

    /**
     * Gets the current retweent count for this tweet
     * @return the retweet count
     */
    public long getRetweetCount() {
        return this.retweetCount;
    }

    /**
     * Sets the screen name for the tweeter
     * @param screenName The user's screen name
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * Gets the tweeter's screen name
     * @return The user's screen name
     */
    public String getScreenName() {
        return this.screenName;
    }

    /**
     * Sets the tweeter's real name
     * @param realName The tweeter's real name
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * Gets the tweeter's real name
     * @return The tweeter's real name
     */
    public String getRealName() {
        return this.realName;
    }

    /**
     * If the tweet quoted another tweet, this is the screen name
     * of the tweeter of the tweet that was quoted
     * @param screenName screen name of the original tweeter
     */
    public void setQuotedAuthorScreenName(String screenName) {
        this.quotedAuthorScreenName = screenName;
    }

    /**
     * Returns the screen name of the tweeter of the tweet that is quoted 
     * by this tweet
     * @return The original tweeter's screen name
     */
    public String getQuotedAuthorScreenName() {
        return this.quotedAuthorScreenName;
    }

    /**
     * Sets the real name of the tweeter of the tweet quoted by this tweet
     * @param realName The original author's real name
     */
    public void setQuotedAuthorRealName(String realName) {
        this.quotedAuthorRealName = realName;
    }

    /**
     * Gets the original tweeter's real name
     * @return The real name of the original tweeter
     */
    public String getQuotedAuthorRealName() {
        return this.quotedAuthorRealName;
    }

    @Override
    public String toString() {
        return "";
        // return "Tweet ID: " + statusId + ", content=" + textContent; // old
    }
}
