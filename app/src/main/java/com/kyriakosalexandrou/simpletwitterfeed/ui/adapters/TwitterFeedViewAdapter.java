package com.kyriakosalexandrou.simpletwitterfeed.ui.adapters;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyriakosalexandrou.simpletwitterfeed.R;
import com.kyriakosalexandrou.simpletwitterfeed.models.Tweet;
import com.kyriakosalexandrou.simpletwitterfeed.ui.custom_views.TwitterFeedView;

import java.util.ArrayList;

/**
 * handles the showing of the tweets
 */
public class TwitterFeedViewAdapter extends RecyclerView.Adapter<TwitterFeedViewAdapter.TweetsViewHolder>
        implements TwitterFeedView.TwitterFeedViewListener {

    private int mTotalTweetsToShow;
    private ArrayList<Tweet> mTweets = new ArrayList<>();

    /**
     * @param totalTweetsToShow number of tweets to show or pass 0 to show all.
     *                          Passing a negative number will throw an {@link IllegalArgumentException}
     */
    public void setUpNumberOfTweetsToShow(int totalTweetsToShow) {
        if (totalTweetsToShow > 0) {
            this.mTotalTweetsToShow = totalTweetsToShow;
        } else if (totalTweetsToShow < 0) {
            throw new IllegalArgumentException("Invalid total number of tweets passed: " + totalTweetsToShow
                    + ". This needs to be equal or greater than 0.");
        }
    }

    @Override
    public void onNewTweetListener(@NonNull Tweet tweet) {
        mTweets.add(0, tweet);
        cleanExcessiveTweets();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private void cleanExcessiveTweets() {
        int tweetsSize = mTweets.size();
        if (mTotalTweetsToShow != 0 && tweetsSize > mTotalTweetsToShow) {
            mTweets.remove(tweetsSize - 1);
        }
    }

    @Override
    public TweetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet_list, parent, false);
        return new TweetsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TweetsViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.mDateTime.setText(tweet.getDateTimeFormatted());
        holder.mPosition.setText(holder.mPosition.getContext().getString(R.string.x, position + 1));
        holder.mUser.setText(tweet.getUser().getName());
        holder.mContent.setText(tweet.getContent());
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public int getNumberOfTweetsToShow() {
        return mTotalTweetsToShow;
    }

    public ArrayList<Tweet> getTweets() {
        return mTweets;
    }

    public void restoreData(ArrayList<Tweet> tweets, int numberOfTweetsToShow) {
        setUpNumberOfTweetsToShow(numberOfTweetsToShow);
        mTweets = tweets;
    }

    static class TweetsViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPosition;
        private final TextView mDateTime;
        private final TextView mUser;
        private final TextView mContent;

        TweetsViewHolder(View itemView) {
            super(itemView);
            mPosition = (TextView) itemView.findViewById(R.id.position);
            mDateTime = (TextView) itemView.findViewById(R.id.date_time);
            mUser = (TextView) itemView.findViewById(R.id.user);
            mContent = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
