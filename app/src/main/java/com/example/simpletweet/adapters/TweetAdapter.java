package com.example.simpletweet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpletweet.R;
import com.example.simpletweet.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    // Constructor
    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView screennameTextView;
        TextView nameTextView;
        TextView bodyTextView;
        TextView timestampTextView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            screennameTextView = itemView.findViewById(R.id.screennameTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }

        public void bind(Tweet tweet) {
            Glide.with(context)
                    .load(tweet.getAuthor().profileImageUrl)
                    .centerCrop() // scale image to fill the entire ImageView
                    .transform(new RoundedCornersTransformation(30, 20))
                    .into(profileImageView);
            screennameTextView.setText(tweet.getAuthor().name);
            nameTextView.setText("@" + tweet.getAuthor().screenName);
            bodyTextView.setText(tweet.getBody());
            timestampTextView.setText(tweet.getCreatedAt());
        }
    }
}
