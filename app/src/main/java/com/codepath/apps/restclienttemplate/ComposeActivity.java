package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wafflecopter.charcounttextview.CharCountTextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.R.id.btnSend;
import static com.codepath.apps.restclienttemplate.R.id.etNewTweet;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Button send = findViewById(btnSend);
        CharCountTextView charCountTextView = findViewById(R.id.tvTextCounter);


        //access twitter client
        client = TwitterApplication.getRestClient(this);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tweet = (EditText) findViewById(etNewTweet);
                String t = tweet.getText().toString();
                sendTweet(t);
            }
        });

       // charCountTextView.setEditText();
        //charCountTextView.
    }

    private void sendTweet(String t) {
        client.sendTweet(t , new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //pull tweet
                    Tweet tweet = Tweet.fromJSON(response);

                    // create intent to move data...
                    Intent newTweet = new Intent();
                    // wraps tweet in parcel
                    newTweet.putExtra("tweet", Parcels.wrap(tweet));
                    // just composed a tweet, sending new tweet back to
                    setResult(RESULT_OK, newTweet);
                    // finishes sends back to main activity (timeline)
                    finish();
                } catch (JSONException e ) {
                    e.printStackTrace();
                }

            }

        });

    }



}
