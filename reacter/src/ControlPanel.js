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

class ControlPanel extends Component {

    render() {
        return (
            <section className="container control-panel">
                <button id="button-top-tweets" onClick={this.props.handleClick} value="popularity" ><i className="fas fa-fire"> popular</i></button>
                <button id="button-recent-tweets" onClick={this.props.handleClick} value="recent" ><i className="fas fa-clock"> recent</i></button>
            </section>)
    }
}

export default ControlPanel; 
