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
import React, { Component } from 'react';
import keygen from 'uuid/v4';

class TweetCard extends Component {

    render() { 
        var tweet= this.props.tweet;
        return ( <div style={this.props.style} key={tweet.key} className='tweet-box'>
                        <div className='tweet-box-content'>
                            <div className='tweet-popularity'>Popularity: {tweet.popularity}</div>
                            <div className='tweet-creation-date'><i className='fab fa-twitter-square'></i> {tweet.creationDate} in {tweet.accountLocation}</div>
                            <div className='tweet-status'>
                                <div className='tweet-username'><i className='fas fa-user-circle'> {tweet.realName}  @{tweet.screenName}</i>:</div>
                                <div className='tweet-text-content'>{tweet.textContent} </div>
                                {tweet.imageUrl != null ?
                                    <div className='tweet-image' ><img alt="" src={tweet.imageUrl} /></div> : ""}
                            </div>
                            <div className='tweet-metadata'>
                                <div className='tweet-metadata-buttons'>
                                    <div className='tweet-metadata-favorites'><i className='fas fa-star'></i> {tweet.favoriteCount}</div>
                                    <div className='tweet-metadata-retweets'><i className='fas fa-retweet'></i> {tweet.retweetCount}</div>
                                </div>
                                {/* <div className= 'tweet-metadata-hashtags'>Hashtag list: {tweet.hashtags}<br/><br/></div>  */}
                                {tweet.sentiment != null ? <div className='tweet-metadata-emoji'>Sentiment: {tweet.sentiment.sentimentEmoji} {tweet.sentiment.sentimentScore}</div> : ""}
                            </div>
                        </div>
                    </div>
 
        )
    }
}
export default TweetCard; 
