package com.example.simpletweet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.simpletweet.R;
import com.example.simpletweet.TwitterApp;
import com.example.simpletweet.TwitterClient;
import com.example.simpletweet.models.Tweet;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";

    TwitterClient client;
    TextInputLayout textInputLayout;
    EditText composeEditText;
    Button postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        textInputLayout = findViewById(R.id.textInputLayout);
        textInputLayout.setCounterMaxLength(MAX_TWEET_LENGTH);
        composeEditText = findViewById(R.id.composeEditText);
        postButton = findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetBody = composeEditText.getText().toString();
                if (tweetBody.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Tweet is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (tweetBody.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getApplicationContext(), "Tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    client.publishTweet(tweetBody, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Intent intent = new Intent();
                                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                                setResult(RESULT_OK, intent);
                                finish();
                                Log.i(TAG, "New tweet: " + tweet.body);
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON exception");
                                e.printStackTrace();
                            }
                            Log.i(TAG, "onSuccess called: " + json.toString());

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure called", throwable);
                        }
                    });
                }
            }
        });
    }
}