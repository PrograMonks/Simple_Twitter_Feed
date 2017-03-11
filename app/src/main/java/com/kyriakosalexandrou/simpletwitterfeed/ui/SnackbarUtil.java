package com.kyriakosalexandrou.simpletwitterfeed.ui;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarUtil {

    public static void showSnackbar(View rootView, @StringRes int description, int duration, @Nullable SnackBarAction snackBarAction) {
        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView, description, duration);

            if (snackBarAction != null) {
                snackbar.setAction(snackBarAction.getText(), snackBarAction.getOnClickListener());
            }

            snackbar.show();
        }
    }

    public static class SnackBarAction {
        private int mText;
        private View.OnClickListener mOnClickListener;

        public SnackBarAction(int text, View.OnClickListener onClickListener) {
            mText = text;
            mOnClickListener = onClickListener;
        }

        int getText() {
            return mText;
        }

        View.OnClickListener getOnClickListener() {
            return mOnClickListener;
        }
    }
}
