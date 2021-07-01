package com.example.simpletweet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpletweet.R;
import com.example.simpletweet.activities.TweetDetailsActivity;
import com.example.simpletweet.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileImageView;
        TextView screennameTextView;
        LinearLayout message_container;
        TextView nameTextView;
        TextView timestampTextView;
        TextView bodyTextView;
        ImageView contentImageView;
        TextView retweetTextView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            screennameTextView = itemView.findViewById(R.id.screennameTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            message_container = itemView.findViewById(R.id.message_container);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
            contentImageView = itemView.findViewById(R.id.contentImageView);
            retweetTextView = itemView.findViewById(R.id.retweetTextView);

            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            Glide.with(context)
                    .load(tweet.getAuthor().profileImageUrl)
                    .circleCrop()
                    .into(profileImageView);
            screennameTextView.setText(tweet.getAuthor().name);
            nameTextView.setText("@" + tweet.getAuthor().screenName);
            int timestampLen = tweet.getCreatedAtRelative().length();
            timestampTextView.setText(" · " + tweet.getCreatedAtRelative());
            if (tweet.getBody().length() == 0) bodyTextView.setHeight(0);
            else bodyTextView.setText(tweet.getBody());
            Glide.with(context)
                    .load(tweet.contentUrl)
                    .into(contentImageView);
            if (tweet.retweetedBy != null) retweetTextView.setText("Retweeted by @"+tweet.retweetedBy);
            else retweetTextView.setHeight(0);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                Tweet tweet = tweets.get(position);

                Intent intent = new Intent(context, TweetDetailsActivity.class);

                // Wrap the tweet in a parcel and attach it to the intent so it can be sent along with it
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));

                Pair<View, String> p1 = Pair.create((View)profileImageView, "profileImageAnimation");
                Pair<View, String> p2 = Pair.create((View)screennameTextView, "screennameAnimation");
                Pair<View, String> p3 = Pair.create((View)nameTextView, "nameAnimation");
                Pair<View, String> p4 = Pair.create((View)bodyTextView, "bodyAnimation");
                Pair<View, String> p5 = Pair.create((View)contentImageView, "contentImageAnimation");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4, p5);
                context.startActivity(intent, options.toBundle());
            }
        }
    }
}
