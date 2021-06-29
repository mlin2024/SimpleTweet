package com.example.simpletweet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.simpletweet.R;
import com.example.simpletweet.TwitterApp;
import com.example.simpletweet.TwitterClient;
import com.example.simpletweet.adapters.TweetAdapter;
import com.example.simpletweet.models.Tweet;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    List<Tweet> tweets;
    TweetAdapter tweetAdapter;
    RecyclerView timelineRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set all member variables to their appropriate values
        client = TwitterApp.getRestClient(this);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(this, tweets);
        timelineRecyclerView = findViewById(R.id.timelineRecyclerView);
        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timelineRecyclerView.setAdapter(tweetAdapter);
        Toolbar timelineToolbar = (Toolbar) findViewById(R.id.timelineToolbar);
        setSupportActionBar(timelineToolbar);
        timelineToolbar.setTitle("Twitter");

        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    tweetAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception");
                    e.printStackTrace();
                }
                Log.i(TAG, "onSuccess called: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure called: " + response, throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutMenuItem) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        client.clearAccessToken();
        Intent intent = new Intent(TimelineActivity.this, LoginActivity.class);
        TimelineActivity.this.startActivity(intent);
    }
}