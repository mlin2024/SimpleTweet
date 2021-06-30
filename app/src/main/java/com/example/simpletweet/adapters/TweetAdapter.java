package com.example.simpletweet.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView screennameTextView;
        TextView nameTextView;
        TextView timestampTextView;
        TextView bodyTextView;
        ImageView contentImageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            screennameTextView = itemView.findViewById(R.id.screennameTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
            contentImageView = itemView.findViewById(R.id.contentImageView);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        public void bind(Tweet tweet) {
            Glide.with(context)
                    .load(tweet.getAuthor().profileImageUrl)
                    .centerCrop() // scale image to fill the entire ImageView
                    .transform(new RoundedCornersTransformation(30, 20))
                    .into(profileImageView);
            screennameTextView.setText(tweet.getAuthor().name);
            nameTextView.setText("@" + tweet.getAuthor().screenName);
            timestampTextView.setMinEms(tweet.getCreatedAt().length());
            timestampTextView.setText(" · " + tweet.getCreatedAt());
            bodyTextView.setText(tweet.getBody());
            Glide.with(context)
                    .load(tweet.contentUrl)
                    .into(contentImageView);
        }
    }
}
