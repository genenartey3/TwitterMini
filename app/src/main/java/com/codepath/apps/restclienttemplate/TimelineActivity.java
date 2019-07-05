package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    // initialize client class creating a reference to clients
    private TwitterClient client;
    // other declarations
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    int REQUEST_CODE= 978;
    // initialize feature for swipe refresh
    SwipeRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    long maxId= 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetAdapter.clear();
                tweets.clear();
                populateTimeline(0);
            }
        });


        // recycler view set up layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this );

        // access twitter client
        client = TwitterApplication.getRestClient(this);

        // find the RecyclerView
        rvTweets = findViewById(R.id.rvTweet);
        rvTweets.setLayoutManager(linearLayoutManager);
        // init the array list (data source)
        tweets = new ArrayList<>();
        //construct the Adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView Setup (layout manager, use adapter)
        rvTweets.setLayoutManager(linearLayoutManager);
        // setup adapter
        rvTweets.setAdapter(tweetAdapter);
        FloatingActionButton composeBtn = findViewById(R.id.composebtn);
        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCompose();
        }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }

        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        populateTimeline(maxId);
    }


    private void populateTimeline(long maxId) {
         //make network request to get back data from twitter API
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // logging to trace flow
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // logging to trace flow
                // Log.d("TwitterClient", response.toString());

                //iterate through JSON array
                for( int i = 0; i < response.length(); i++) {
                    // for each object deserialize JSON object
                    // convert each object to a tweet model
                    // add that tweet model to our data source
                    // notify adapter that we've added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    }  catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });

    }

    public void toCompose() {
        //traces activity movement from timeline
        Intent intent = new Intent(this, ComposeActivity.class);
        // starts new activity and expects result after finish
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check request code and result code first
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            //use the data parameter to get tweet
            Tweet tweet = (Tweet) data.getSerializableExtra("tweet");

            // insert tweet into array list
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);

        }
    }

    public void loadNextDataFromApi(int offset) {
        //get id of last tweet in array list
        maxId = tweets.get(tweets.size()-1).uid;
        populateTimeline(maxId);
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }
}
