package com.example.simpletweet.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.simpletweet.EndlessRecyclerViewScrollListener;
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
    public static final int NUM_TO_LOAD = 25;

    long lowest_max_id;
    TwitterClient client;
    List<Tweet> tweets;
    TweetAdapter tweetAdapter;
    Toolbar timelineToolbar;
    LinearLayoutManager linearLayoutManager;
    RecyclerView timelineRecyclerView;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set all member variables to their appropriate values
        lowest_max_id = Long.MAX_VALUE;
        client = TwitterApp.getRestClient(this);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(this, tweets);
        timelineToolbar = findViewById(R.id.timelineToolbar);
        setSupportActionBar(timelineToolbar);
        linearLayoutManager = new LinearLayoutManager(this);
        timelineRecyclerView = findViewById(R.id.timelineRecyclerView);
        timelineRecyclerView.setLayoutManager(linearLayoutManager);
        timelineRecyclerView.setAdapter(tweetAdapter);
        swipeContainer = findViewById(R.id.swipeContainer);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateHomeTimeline();
            }
        };

        timelineRecyclerView.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    ArrayList<Tweet> newTweets = new ArrayList<>();
                    newTweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    for (int i = 0; i < newTweets.size(); i++) {
                        long id = newTweets.get(i).getId();
                        lowest_max_id = Math.min(lowest_max_id, id);
                    }
                    tweets.addAll(newTweets);
                    if (tweets.size() < NUM_TO_LOAD) tweetAdapter.notifyDataSetChanged();
                    else tweetAdapter.notifyItemRangeInserted(Math.max(0, tweets.size() - NUM_TO_LOAD), Math.min(tweets.size(), NUM_TO_LOAD));
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
        }, lowest_max_id - 1);
    }

    public void fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    // Clear out old items before appending in the new ones
                    tweetAdapter.clear();
                    // Add new items to your adapter
                    tweetAdapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    // Signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception");
                    e.printStackTrace();
                }
                Log.i(TAG, "onSuccess called");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure called: " + response, throwable);
            }
        }, 0);
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