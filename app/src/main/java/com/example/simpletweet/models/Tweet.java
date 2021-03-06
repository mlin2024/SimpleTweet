package com.example.simpletweet.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Parcel
public class Tweet {
    public static final String TAG = "Tweet";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String body, createdAt, contentUrl, retweetedBy;
    public User author;
    public long id;

    // Constructor
    public Tweet(JSONObject jsonObject) throws JSONException {
        this.fromJson(jsonObject);
    }

    // Empty constructor
    public Tweet() {};

    // Returns a single tweet from a JSON object
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        if (jsonObject.has("retweeted_status")) {
            tweet.retweetedBy = User.fromJson(jsonObject.getJSONObject("user")).name;
            jsonObject = jsonObject.getJSONObject("retweeted_status");
        }
        else tweet.retweetedBy = null;
        if (jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        }
        else {
            tweet.body = jsonObject.getString("text");
        }

        // Logic to remove image URLs from tweet body
        Pattern url = Pattern.compile("https://t.co/.*");
        Matcher body = url.matcher(tweet.body);
        if (body.find()) tweet.body = tweet.body.replaceAll("https://t.co/.*", "");

        tweet.createdAt = jsonObject.getString("created_at");
        if (!jsonObject.getJSONObject("entities").has("media")) tweet.contentUrl = null;
        else tweet.contentUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
        tweet.author =  User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = Long.parseLong(jsonObject.getString("id"));
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
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        try {
            Date date = sf.parse(createdAt);
            String[] arr = date.toString().split(" ");
            int hour = date.getHours();
            int min = date.getMinutes();
            String time = "";
            if (hour > 12) {
                hour -= 12;
                time = hour + ":" + min + " PM";
            }
            else {
                time = hour + ":" + min + " AM";
            }
            return arr[0] + " " + arr[1] + " " + arr[2] + " ?? " + time;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getCreatedAtRelative() {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(createdAt).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + "m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + "h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + "d";
                }
            } catch (ParseException e) {
                Log.i(TAG, "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public User getAuthor() {
        return author;
    }

    public long getId() {
        return id;
    }
}
