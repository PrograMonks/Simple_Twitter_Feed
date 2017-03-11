package com.kyriakosalexandrou.simpletwitterfeed.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kyriakosalexandrou.simpletwitterfeed.R;
import com.kyriakosalexandrou.simpletwitterfeed.ui.SnackbarUtil;
import com.kyriakosalexandrou.simpletwitterfeed.ui.custom_views.StatusBarView;
import com.kyriakosalexandrou.simpletwitterfeed.ui.custom_views.TwitterFeedView;

/**
 * Created by Kiki on 07/03/2017.
 */
public class TwitterFeedFragment extends Fragment implements TwitterFeedView.ConnectionStatusListener {
    private static final String KEYWORDS_KEY = "keywords_key";
    private static final String TOTAL_NUMBER_OF_TWEETS_TO_SHOW_KEY = "total_number_of_tweets_to_show";
    private TwitterFeedView mTwitterFeedView;
    private StatusBarView mConnectionStatusBar;
    private String[] mKeywords;
    private int mTotalNumberOfTweets;

    public static Fragment newInstance(String[] keywords, int totalNumberOfTweets) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(KEYWORDS_KEY, keywords);
        bundle.putInt(TOTAL_NUMBER_OF_TWEETS_TO_SHOW_KEY, totalNumberOfTweets);

        TwitterFeedFragment fragment = new TwitterFeedFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter_feed, container, false);

        mConnectionStatusBar = (StatusBarView) view.findViewById(R.id.connection_status_bar);
        mTwitterFeedView = (TwitterFeedView) view.findViewById(R.id.twitter_feed_view);

        setUpDataFromArguments();

        mTwitterFeedView.initialise(mTotalNumberOfTweets, this);

        mConnectionStatusBar.initialise(
                getString(R.string.connected), ContextCompat.getColor(getContext(), android.R.color.holo_green_dark),
                getString(R.string.disconnected), ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));

        return view;
    }

    private void setUpDataFromArguments() {
        Bundle arguments = getArguments();
        mKeywords = arguments.getStringArray(KEYWORDS_KEY);
        mTotalNumberOfTweets = arguments.getInt(TOTAL_NUMBER_OF_TWEETS_TO_SHOW_KEY);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTwitterFeedView.cleanUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        readBankingTweets();
    }

    private void readBankingTweets() {
        mTwitterFeedView.readTweetStream(mKeywords);
    }

    @Override
    public void onConnectionStatus(TwitterFeedView.ConnectionStatus connectionStatus) {

        switch (connectionStatus) {
            case STREAM_CLOSED_OR_NO_NETWORK_CONNECTION:
                mTwitterFeedView.cleanUp();

                SnackbarUtil.showSnackbar(mConnectionStatusBar, R.string.network_error_long_descr, Snackbar.LENGTH_INDEFINITE,
                        new SnackbarUtil.SnackBarAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                readBankingTweets();
                            }
                        }));
                break;

            case EXCEEDED_CONNECTION_LIMIT_FOR_USER:
                SnackbarUtil.showSnackbar(mConnectionStatusBar, R.string.exceeded_connection_limit_for_user, Snackbar.LENGTH_LONG, null);
                break;

            case UNKNOWN_EXCEPTION:
                SnackbarUtil.showSnackbar(mConnectionStatusBar, R.string.unknown_error, Snackbar.LENGTH_LONG, null);
                break;
        }

        mConnectionStatusBar.updateState(mTwitterFeedView.isConnected());
    }
}
