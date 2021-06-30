package com.example.simpletweet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simpletweet.R;
import com.example.simpletweet.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailsActivity extends AppCompatActivity {
    public static final String TAG = "TweetDetailsActivity";

    Tweet tweet;

    ImageView profileImageView;
    TextView screennameTextView;
    TextView nameTextView;
    TextView bodyTextView;
    ImageView contentImageView;
    TextView timestampTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        profileImageView = findViewById(R.id.profileImageView);
        screennameTextView = findViewById(R.id.screennameTextView);
        nameTextView = findViewById(R.id.nameTextView);
        bodyTextView = findViewById(R.id.bodyTextView);
        contentImageView = findViewById(R.id.contentImageView);
        timestampTextView = findViewById(R.id.timestampTextView);

        // Unwrap the movie that was passed in by the intent
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        Log.d(TAG, "Showing details for tweet by " + tweet.getAuthor().screenName + "(" + tweet.getAuthor().name + ")");

        Glide.with(this)
                .load(tweet.getAuthor().profileImageUrl)
                .centerCrop() // scale image to fill the entire ImageView
                .transform(new RoundedCornersTransformation(30, 20))
                .into(profileImageView);
        screennameTextView.setText(tweet.getAuthor().name);
        nameTextView.setText("@" + tweet.getAuthor().screenName);
        bodyTextView.setText(tweet.getBody());
        Glide.with(this)
                .load(tweet.contentUrl)
                .into(contentImageView);
        timestampTextView.setText(tweet.getCreatedAt());
    }
}