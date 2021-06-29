package com.example.simpletweet.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    public String name, screenName, profileImageUrl;

    // Constructor
    public User(JSONObject jsonObject) throws JSONException {
        this.fromJson(jsonObject);
    }

    // Empty constructor
    public User() {};

    // Returns a single user from a JSON object
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
