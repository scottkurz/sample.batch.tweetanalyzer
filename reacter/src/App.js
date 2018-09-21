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
import AppHelmet from "./AppHelmet";
import ControlPanel from "./ControlPanel"
import 'font-awesome/css/font-awesome.min.css';
import { Trail } from 'react-spring';
import keygen from 'uuid/v4';

import TweetContainer from './TweetContainer';
// var FontAwesome = require('react-fontawesome');

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      tweets: [],
      category: "popularity"
    };

    this.getTweets = this.getTweets.bind(this);
    this.getTweetsEvent = this.getTweetsEvent.bind(this);
  }

  getTweets() {
    var start = 0;
    var end = 100;
    fetch('http://localhost:9080/web/api/tweets/' + "?from=" + start + "&to=" + end + "&orderBy=" + this.state.category).then(response => {
      return response.json();
    }).then(json => {
      var tweets = []
      for (var i = 0; i < json.length; i++) {
        json[i].key = keygen();
        tweets.push(json[i]);
      }
      this.setState({ tweets: tweets });
    }).catch(err => {
      console.log("Something went wrong retrieving the json.")
    });
  }
  getTweetsEvent(e) {
    // console.log(e.target.value)
    this.setState({category: e.target.value},  () => {this.getTweets(this.state.category)});
  }
  componentDidMount() {
    this.getTweets();
  }
  render() {
    return (
      <div className="App wrapper">
        <AppHelmet />
        <div className="container page-container">
          <ControlPanel handleClick={this.getTweetsEvent} />

          {/* <Trail from={{ opacity: 0 }} to={{ opacity: 1 }} keys={this.state.tweets.map(tweet => tweet.key)}> */}
          <TweetContainer tweets={this.state.tweets} handleClick={this.getTweetsEvent} />
          {/* </Trail> */}

        </div>
      </div>
    );
  }
}

export default App;
