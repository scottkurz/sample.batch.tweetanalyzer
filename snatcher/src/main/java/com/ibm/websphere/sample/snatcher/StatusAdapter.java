package com.ibm.websphere.sample.snatcher;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

import twitter4j.Status;
import twitter4j.User;

public class StatusAdapter implements JsonbAdapter<Status, JsonObject> { 

	private static final String dateTimeFormatPattern = "yyyy/MM/dd HH:mm:ss z";
	private static final DateTimeFormatter dateTimeFormatter = 
			DateTimeFormatter.ofPattern(dateTimeFormatPattern)
				.withZone(ZoneId.of("America/New_York"));
	@Override
	public Status adaptFromJson(JsonObject arg0) throws Exception {

		// We have other plans for going from JSON to Java
		return null;
	}

	@Override
	public JsonObject adaptToJson(Status status) throws Exception {
		
		User user = status.getUser();
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
                .add("location", user.getLocation())
                .add("followers_count", user.getFollowersCount())
                .build();
    }
}