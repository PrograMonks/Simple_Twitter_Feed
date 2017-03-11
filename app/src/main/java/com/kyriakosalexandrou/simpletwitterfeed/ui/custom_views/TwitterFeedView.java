package com.kyriakosalexandrou.simpletwitterfeed.ui.custom_views;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.kyriakosalexandrou.simpletwitterfeed.BuildConfig;
import com.kyriakosalexandrou.simpletwitterfeed.R;
import com.kyriakosalexandrou.simpletwitterfeed.models.Tweet;
import com.kyriakosalexandrou.simpletwitterfeed.ui.adapters.TwitterFeedViewAdapter;

import java.util.ArrayList;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Kiki on 05/03/2017.
 * A custom view to handle twitter feeds
 */
public class TwitterFeedView extends FrameLayout {
    private static final String TAG = TwitterFeedView.class.getSimpleName();
    private Context mContext;
    private TwitterFeedViewListener mTwitterFeedViewListener;
    private TwitterStream mTwitterStream;
    private TwitterFeedViewAdapter mTwitterFeedViewAdapter;
    private ConnectionStatusListener mConnectionStatusListener;
    private ConnectionStatus mConnectionStatus = ConnectionStatus.INITIAL;

    public TwitterFeedView(@NonNull Context context) {
        this(context, null);
    }

    public TwitterFeedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwitterFeedView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TwitterFeedView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void initialise(int totalTweetsToShow, ConnectionStatusListener connectionStatusListener) {
        mTwitterFeedViewAdapter.setUpNumberOfTweetsToShow(totalTweetsToShow);
        mConnectionStatusListener = connectionStatusListener;
    }

    private void init(@NonNull Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.view_twitter_feed, this);
        setUpRecyclerView();
        setUpTwitterStream();
    }

    private void setUpRecyclerView() {
        RecyclerView tweetsRecyclerView = (RecyclerView) findViewById(R.id.tweets_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        tweetsRecyclerView.setLayoutManager(mLayoutManager);
        tweetsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTwitterFeedViewAdapter = new TwitterFeedViewAdapter();
        mTwitterFeedViewListener = mTwitterFeedViewAdapter;
        tweetsRecyclerView.setAdapter(mTwitterFeedViewAdapter);
    }

    private void setUpTwitterStream() {
        ConfigurationBuilder mConfigurationBuilder = new ConfigurationBuilder();
        mConfigurationBuilder.setOAuthConsumerKey(BuildConfig.CONSUMER_KEY)
                .setOAuthConsumerSecret(BuildConfig.CONSUMER_SECRET)
                .setOAuthAccessToken(BuildConfig.TOKEN)
                .setOAuthAccessTokenSecret(BuildConfig.SECRET);

        mTwitterStream = new TwitterStreamFactory(mConfigurationBuilder.build()).getInstance();
    }

    /**
     * starts a read stream based on the given keywords
     *
     * @param keywords the keywords to search for
     */
    public void readTweetStream(@NonNull String[] keywords) {
        mTwitterStream.addListener(new StatusListener() {

            @Override
            public void onStatus(Status status) {
                //got a new tweet, i assume that means we are connected
                if (!isConnected()) {
                    mConnectionStatus = ConnectionStatus.CONNECTED;
                    initiateOnConnectionStatus();
                }

                final Tweet tweet = new Tweet(status.getUser(), status.getText(), status.getCreatedAt());
                Log.v(TAG, "User: " + tweet.getUser().getName() + " Tweet: " + tweet.getContent()
                        + " Date: " + tweet.getDateTimeFormatted()
                );

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mTwitterFeedViewListener.onNewTweetListener(tweet);
                    }
                });
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
            }

            @Override
            public void onException(Exception e) {
                if (e instanceof TwitterException) {
                    TwitterException twitterException = (TwitterException) e;

                    int statusCode = twitterException.getStatusCode();
                    // https://dev.twitter.com/overview/api/response-codes
                    if (statusCode == -1) {
                        mConnectionStatus = ConnectionStatus.STREAM_CLOSED_OR_NO_NETWORK_CONNECTION;
                    } else if (statusCode == 420) {
                        mConnectionStatus = ConnectionStatus.EXCEEDED_CONNECTION_LIMIT_FOR_USER;
                    } else {
                        mConnectionStatus = ConnectionStatus.UNKNOWN_EXCEPTION;
                    }

                    initiateOnConnectionStatus();
                }
            }
        });

        FilterQuery fq = new FilterQuery();
        fq.track(keywords);
        mTwitterStream.filter(fq);
    }

    private void initiateOnConnectionStatus() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mConnectionStatusListener.onConnectionStatus(
                        mConnectionStatus);
            }
        });
    }

    public boolean isConnected() {
        return mConnectionStatus == ConnectionStatus.CONNECTED;
    }

    public void cleanUp() {
        mTwitterStream.clearListeners();
        mTwitterStream.cleanUp();
        mTwitterStream.shutdown();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.number = mTwitterFeedViewAdapter.getNumberOfTweetsToShow();
        ss.tweets = mTwitterFeedViewAdapter.getTweets();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        int numberOfTweetsToShow = ss.number;
        ArrayList<Tweet> tweets = ss.tweets;

        if (mTwitterFeedViewAdapter != null) {
            mTwitterFeedViewAdapter.restoreData(tweets, numberOfTweetsToShow);
        }

        super.onRestoreInstanceState(state);
    }

    private static class SavedState extends BaseSavedState {
        int number;
        ArrayList<Tweet> tweets;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            number = in.readInt();
            //noinspection unchecked
            tweets = in.readArrayList(Tweet.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(number);
            out.writeList(tweets);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public enum ConnectionStatus {
        INITIAL, CONNECTED, STREAM_CLOSED_OR_NO_NETWORK_CONNECTION, EXCEEDED_CONNECTION_LIMIT_FOR_USER, UNKNOWN_EXCEPTION
    }


    public interface TwitterFeedViewListener {
        /**
         * called when a new tweet is retrieved. Runs on the UI thread
         */
        void onNewTweetListener(@NonNull Tweet tweet);
    }

    public interface ConnectionStatusListener {
        /**
         * callback for connection status, runs on the UI thread.
         *
         * @param connectionStatus can be one of<br/>
         *                         {@link ConnectionStatus#INITIAL}<br/>
         *                         {@link ConnectionStatus#CONNECTED}
         *                         {@link ConnectionStatus#STREAM_CLOSED_OR_NO_NETWORK_CONNECTION}<br/>
         *                         {@link ConnectionStatus#EXCEEDED_CONNECTION_LIMIT_FOR_USER}<br/>
         *                         {@link ConnectionStatus#UNKNOWN_EXCEPTION}
         */
        void onConnectionStatus(ConnectionStatus connectionStatus);
    }
}
