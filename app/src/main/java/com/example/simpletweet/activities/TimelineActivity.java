package com.example.simpletweet.activities;

import androidx.annotation.Nullable;
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
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";
    private final int COMPOSE_REQUEST_CODE = 413;

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

        switch (id) {
            case R.id.logoutMenuItem:
                logout();
                return true;

            case R.id.composeMenuItem:
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                TimelineActivity.this.startActivityForResult(intent, COMPOSE_REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case COMPOSE_REQUEST_CODE:
                    Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
                    tweets.add(0, tweet);
                    tweetAdapter.notifyItemInserted(0);
                    timelineRecyclerView.smoothScrollToPosition(0);
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void logout() {
        client.clearAccessToken();
        Intent intent = new Intent(TimelineActivity.this, LoginActivity.class);
        TimelineActivity.this.startActivity(intent);
    }
}