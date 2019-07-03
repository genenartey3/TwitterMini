package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    // list attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    // de-serialize JSON
    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        // constructs a new user object called user
        User user = new User();

        // extract JSON data values
        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");

        //returns constructed user object
        return user;
    }
}