package com.kyriakosalexandrou.simpletwitterfeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;

import twitter4j.User;

/**
 * Created by Kiki on 05/03/2017.
 * Instead of passing around the whole {@link twitter4j.Status} object we get from the Twitter API we use this
 * {@link Tweet} model. In this way we can hide any unnecessary data that is not needed and improve on it for
 * easy access to other data that needs to be manipulated such as the formatted date and time
 */
public class Tweet implements Parcelable {
    private final User mUser;
    private final String mContent;
    private final String mDateTimeFormatted;
    private final Date mDateTime;

    public Tweet(User user, String content, Date dateTime) {
        mUser = user;
        mContent = content;
        mDateTime = dateTime;

        mDateTimeFormatted = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT).format(mDateTime);
    }

    protected Tweet(Parcel in) {
        mUser = (User) in.readSerializable();
        mContent = in.readString();
        mDateTime = (Date) in.readSerializable();
        mDateTimeFormatted = in.readString();
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    public User getUser() {
        return mUser;
    }

    public String getContent() {
        return mContent;
    }

    public String getDateTimeFormatted() {
        return mDateTimeFormatted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mUser);
        dest.writeString(mContent);
        dest.writeSerializable(mDateTime);
        dest.writeString(mDateTimeFormatted);
    }
}
