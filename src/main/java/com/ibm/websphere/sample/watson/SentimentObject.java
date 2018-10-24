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
package com.ibm.websphere.sample.watson;


import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * A container class to hold the results of IBM Watson sentiment analysis
 * @author Cassandra Newcomer
 */
@Embeddable
public class SentimentObject implements Serializable {

	private static final long serialVersionUID = 8780143987952920718L;

	@Basic
    @Column(name = "SENTIMENT_LABEL")
	String sentimentLabel;

	@Basic
    @Column(name = "SENTIMENT_SCORE")
    Double sentimentScore;

	@Basic
    @Column(name = "SENTIMENT_EMOJI")
    String sentimentEmoji;

    /**
     * Default constructor which sets up a neutral result
     */
    public SentimentObject() {
    	this.sentimentLabel = "";
    	this.sentimentScore = 0.0;
    	setSentimentEmoji();
    }

    /**
     * Constructor to set an input sentiment
     * @param label The label returned by Watson
     * @param score The score returned by Watson
     */
    public SentimentObject(String label, Double score) {
        this.sentimentLabel = label;
        this.sentimentScore = score;
        setSentimentEmoji();
    }

    /**
     * For fun, set an emoji based on the sentiment value
     */
    public void setSentimentEmoji() {
        
        String neutralEmoji = "😶";

        String slightlyHappyEmoji = "🙂";
        String veryHappyEmoji = "😃";
        String veryExtremelyHappyEmoji = "😁";

        String slightlyUnhappyEmoji = "🙁";
        String veryUnhappyEmoji = "😠"; 
        String veryExtremelyUnhappyEmoji = "😡";

        Double ss = this.sentimentScore;
        if (ss > .9) { // greater than .9
            this.sentimentEmoji = veryExtremelyHappyEmoji;
        } else if (ss > .6 && ss <= .9) { // between .9 and .6
            this.sentimentEmoji = veryHappyEmoji;
        } else if (ss > .3 && ss <= .6) { // between .6 and .3
            this.sentimentEmoji = slightlyHappyEmoji;
        } else if (ss < -.3 && ss > -.6) { // between -.3 and -.6
            this.sentimentEmoji = slightlyUnhappyEmoji;
        } else if (ss < -.6 && ss > -.9) { // between -.6 and -.9
            this.sentimentEmoji = veryUnhappyEmoji;
        } else if (ss < -.9) { // less than -.9
            this.sentimentEmoji = veryExtremelyUnhappyEmoji;
        } else { // between .3 and -.3
            this.sentimentEmoji = neutralEmoji;
        }
    }

    /**
     * Gets the emoji for this sentiment
     * @return A string containing an emoji
     */
    public String getSentimentEmoji() {
        return this.sentimentEmoji;
    }

    /**
     * Sets the label for this sentiment (probably from Watson)
     * @param label The string label from Watson
     */
    public void setSentimentLabel(String label) {
        this.sentimentLabel = label;
    }

    /**
     * Gets the sentiment label for this sentiment
     * @return The string label from Watson
     */
    public String getSentimentLabel() {
        return this.sentimentLabel;
    }

    /**
     * gets the sentiment score (from Watson)
     * @return a sentiment value from -1 to +1 (negative to positive sentiment)
     */
    public Double getSentimentScore() {
        return this.sentimentScore;
    }

    /**
     * Sets the sentiment score, probably using a value from Watson
     * @param score The sentiment score, expected to be a value from -1 to +1, but not checked
     */
    public void setSentimentScore(Double score) {
        this.sentimentScore = score;
        setSentimentEmoji();
    }

}
