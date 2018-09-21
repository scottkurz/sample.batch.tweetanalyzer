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
import { Spring } from 'react-spring';
import { Transition } from 'react-spring';
import { Trail } from 'react-spring';
import TweetCard from './TweetCard';
import LoadCard from './LoadCard';

class TweetContainer extends Component {

    render() {
        var tweets = this.props.tweets;
        return (
            <section id="tweet-container" className="container tweet-container" >
                {tweets.map(tweet => <TweetCard key ={tweet.key}tweet={tweet} />)}
                <LoadCard handleClick={this.props.handleClick} />
            </section>)
    }
}
export default TweetContainer;

{/* <Trail from={{ opacity: 0 }} to={{ opacity: 1 }} keys={items.map(item => item.key)}>
   {items.map(item => styles => <TweetCard style={styles}  tweet={item}/>)}
     </Trail> */}
