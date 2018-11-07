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

import javax.annotation.PostConstruct;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.ibm.websphere.sample.jpa.TweetDataObject;
import com.ibm.websphere.sample.watson.LanguageAnalyzer;
import com.ibm.websphere.sample.watson.SentimentObject;

/**
 * This class implements an ItemProcessor to process Twitter Status objects.
 * Selected fields from the Status object are moved into a JPA persistent object (TweetDataObject).
 * If Watson connection information is available, a SentimentObject will be created based on
 * Watson analysis of the tweet text.
 * 
 * @author Cassandra Newcomer
 * @author David Follis
 */
@Dependent
public class SentimentAnalysisProcessor implements ItemProcessor {
	
	LanguageAnalyzer la = null;

    @Inject
    @BatchProperty(name = "useWatson")
	String useWatsonProp;
	Boolean useWatson;
	
    @Inject
    @BatchProperty(name = "WatsonPropFile")
    String watsonPropFile;
	
    @PostConstruct
    private void readProps() {
    	useWatson = Boolean.parseBoolean(useWatsonProp);
    }

	@Override
	public Object processItem(Object readItem) throws Exception {

		TweetDataObject to = (TweetDataObject)readItem; 
		
		// First time through, see if we can use Watson
		if ((useWatson==true)&&(la==null)) {
			if (watsonPropFile!=null) {
				la = new LanguageAnalyzer(watsonPropFile);
			} else {
				// guess not...
				useWatson=false;
			}
		}
		
        // If we're set up to use Watson, get some sentiment analysis done
        if (useWatson) {
            try {
                SentimentObject so = la.analyze(to.getTextContent());
                to.setSentimentObject(so);
            } catch (Exception e) {
            	// Ah well, just use the default SentimentObject (neutral)
                SentimentObject so = new SentimentObject();
                to.setSentimentObject(so);
            }
        } 
        
        return to;
	}

}
