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
import { Helmet } from "react-helmet";
class AppHelmet extends Component {

    render() {
        return (
            <div>
                <Helmet defer={false}>
                    <link href="https://fonts.googleapis.com/css?family=Crimson+Text:400,400i,600,600i,700,700i|Open+Sans:300,300i,400,400i,600,600i,700,700i,800,800i" rel="stylesheet" />
                    <link rel="stylesheet" type="text/css" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous" />
                </Helmet>
                <Helmet>                
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
                    <title>IBM - TweetAnalyzer</title></Helmet>
            </div>)
    }
}
export default AppHelmet; 
