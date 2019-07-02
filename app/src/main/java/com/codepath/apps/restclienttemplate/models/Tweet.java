package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {

    // attributes of a tweet listed
    public String body;
    public long uid; // database id for tweet
    public User user;
    public String createdAt;

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {

        // construct new tweet obj called tweet
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        // extracts user defined in  user class
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        //returns constructed tweet object
        return tweet;


    }

}
