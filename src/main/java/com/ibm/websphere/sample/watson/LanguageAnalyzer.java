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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;

/**
 * Interacts with the IBM Watson Natural Language Understanding Sentiment Analyzer to get a
 * sentiment value (from -1 to 1) for an input string
 * @author Cassandra Newcomer
 */
public class LanguageAnalyzer {

    NaturalLanguageUnderstanding service;
    private String propertiesFile;
    Properties props = new Properties();
    
    /**
     * Constructor that sets up our connection to the Watson service
     * @param pf A full path to a properties file containing values needed to connect to Watson (userid/password)
     * @throws Exception If anything goes wrong :-)
     */
    public LanguageAnalyzer(String pf) throws Exception {
    	propertiesFile = pf;
		InputStream input = new FileInputStream(propertiesFile);
		props.load(input);		
		service = new NaturalLanguageUnderstanding(props.getProperty("version"),props.getProperty("username"), 
				props.getProperty("password"));    	
    }

    /**
     * Uses Watson to analyze the string
     * @param text The text string to analyze
     * @return A SentimentObject containing the resulting label and score (-1 (bad) to +1 (positive))
     */
    public SentimentObject analyze(String text) {

        SentimentOptions sentiment = new SentimentOptions.Builder().build();

        Features features = new Features.Builder().sentiment(sentiment).build();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(text).features(features).build();

        AnalysisResults response = service.analyze(parameters).execute();

        String label = response.getSentiment().getDocument().getLabel();
        Double score = response.getSentiment().getDocument().getScore();
        return new SentimentObject(label, score);
    }
}
