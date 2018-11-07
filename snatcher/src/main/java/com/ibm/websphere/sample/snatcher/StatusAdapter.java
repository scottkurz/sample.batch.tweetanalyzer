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
package com.ibm.websphere.sample.snatcher;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

import twitter4j.Status;
import twitter4j.User;


/**
 * An implementation of JsonbAdapter that serializes to JSON a <code>twitter4j.Status</code> object,
 * 
 * 
 * @author Scott Kurz
 */
public class StatusAdapter implements JsonbAdapter<Status, JsonObject> { 

	/**
	 * Constant serialized if location not present with user data
	 */
	public static final String UNKNOWN_LOCATION = "<unknown location>";

	private static final String dateTimeFormatPattern = "yyyy/MM/dd HH:mm:ss z";
	private static final DateTimeFormatter dateTimeFormatter = 
			DateTimeFormatter.ofPattern(dateTimeFormatPattern)
				.withZone(ZoneId.of("America/New_York"));
	/**
	 * Not used, returns <null> always.
	 */
	@Override
	public Status adaptFromJson(JsonObject arg0) throws Exception {

		// We have other plans for going from JSON to Java
		return null;
	}

	/**
	 * Serialize to JSON, picking out the fields we care about (see implementation).
	 * 
	 * @param status input object
	 * @return serialized JsonObject
	 */
	@Override
	public JsonObject adaptToJson(Status status) throws Exception {
		
		User user = status.getUser();

		// Treat this one field specially since it is somewhat common to find a null
		String location = user.getLocation();
		String writeLocation = location != null ? location : UNKNOWN_LOCATION;
		
		final Instant instant = status.getCreatedAt().toInstant();
		final String formattedInstance = dateTimeFormatter.format(instant);
		   
        return Json.createObjectBuilder()
                .add("status_id", status.getId())               
                .add("creation_date", formattedInstance)                                         
                .add("text", status.getText())         
        		.add("retweet_count", status.getRetweetCount())
                .add("favorite_count", status.getFavoriteCount())
                .add("account_id", user.getId())
                .add("real_name", user.getName())
                .add("screen_name", user.getScreenName())
                .add("location", writeLocation)
                .add("followers_count", user.getFollowersCount())
                .build();
    }
}