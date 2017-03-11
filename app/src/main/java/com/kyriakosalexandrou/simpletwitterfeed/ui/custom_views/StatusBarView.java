package com.kyriakosalexandrou.simpletwitterfeed.ui.custom_views;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kyriakosalexandrou.simpletwitterfeed.R;

/**
 * Created by Kiki on 05/03/2017.
 * A custom view to handle status in a horizontal bar
 */
public class StatusBarView extends FrameLayout {
    private boolean mState = false;
    private TextView mStatus;
    private String mEnabledText;
    private int mEnabledBgColor;
    private String mDisabledText;
    private int mDisabledBgColor;


    public StatusBarView(@NonNull Context context) {
        this(context, null);
    }

    public StatusBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarView(@NonNull Context context, @Nullable AttributeSet attrs,
                         @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusBarView(@NonNull Context context, @Nullable AttributeSet attrs,
                         @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_status_bar, this);
        mStatus = (TextView) view.findViewById(R.id.status_bar);
    }

    public void initialise(String enabledText, int enabledBgColor,
                           String disabledText, int disabledBgColor) {
        mEnabledText = enabledText;
        mEnabledBgColor = enabledBgColor;
        mDisabledText = disabledText;
        mDisabledBgColor = disabledBgColor;
    }

    public void updateState(boolean enable) {
        mState = enable;

        if (mState) {
            mStatus.setText(mEnabledText);
            mStatus.setBackgroundColor(mEnabledBgColor);
        } else {
            mStatus.setText(mDisabledText);
            mStatus.setBackgroundColor(mDisabledBgColor);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        StatusBarView.SavedState ss = new StatusBarView.SavedState(superState);
        ss.state = mState;
        ss.enabledText = mEnabledText;
        ss.enabledBgColor = mEnabledBgColor;
        ss.disabledText = mDisabledText;
        ss.disabledBgColor = mDisabledBgColor;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        StatusBarView.SavedState ss = (StatusBarView.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mState = ss.state;
        mEnabledText = ss.enabledText;
        mEnabledBgColor = ss.enabledBgColor;
        mDisabledText = ss.disabledText;
        mDisabledBgColor = ss.disabledBgColor;

        updateState(mState);

        super.onRestoreInstanceState(state);
    }

    private static class SavedState extends BaseSavedState {
        boolean state;
        String enabledText;
        int enabledBgColor;
        String disabledText;
        int disabledBgColor;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            state = (Boolean) in.readValue(Boolean.class.getClassLoader());
            enabledText = in.readString();
            enabledBgColor = in.readInt();
            disabledText = in.readString();
            disabledBgColor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(state);
            out.writeString(enabledText);
            out.writeInt(enabledBgColor);
            out.writeString(disabledText);
            out.writeInt(disabledBgColor);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public StatusBarView.SavedState createFromParcel(Parcel in) {
                return new StatusBarView.SavedState(in);
            }

            public StatusBarView.SavedState[] newArray(int size) {
                return new StatusBarView.SavedState[size];
            }
        };
    }
}