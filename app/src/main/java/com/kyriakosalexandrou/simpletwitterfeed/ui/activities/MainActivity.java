package com.kyriakosalexandrou.simpletwitterfeed.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.kyriakosalexandrou.simpletwitterfeed.R;
import com.kyriakosalexandrou.simpletwitterfeed.ui.fragments.TwitterFeedFragment;

public class MainActivity extends AppCompatActivity {
    private static final String[] KEYWORDS = {"banking"};
    private static final int TOTAL_NUMBER_OF_TWEETS_TO_SHOW = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            addFragment(TwitterFeedFragment.newInstance(KEYWORDS, TOTAL_NUMBER_OF_TWEETS_TO_SHOW));
        }
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
