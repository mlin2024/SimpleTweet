package com.example.simpletweet.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body, createdAt;
    public User author;

    // Constructor
    public Tweet(JSONObject jsonObject) throws JSONException {
        this.fromJson(jsonObject);
    }

    // Empty constructor
    public Tweet() {};

    // Returns a single tweet from a JSON object
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.author =  User.fromJson(jsonObject.getJSONObject("user"));
        return tweet;
    }

    // Returns a list of tweets from a list of JSON objects
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Tweet> tweets  = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getAuthor() {
        return author;
    }
}