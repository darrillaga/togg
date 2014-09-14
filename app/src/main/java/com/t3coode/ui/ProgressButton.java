package com.t3coode.ui;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.t3coode.togg.R;

public class ProgressButton extends FrameLayout implements ProgressView {

    private Button mButton;
    private ProgressBar mPreloader;
    private boolean mPreloaderShown;

    public ProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        initContent(context, attrs);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context);
        initContent(context, attrs);
    }

    public ProgressButton(Context context) {
        super(context);
        initContent(context, null);
    }

    private void initContent(Context context, AttributeSet attrs) {
        if (attrs != null) {
            setId(attrs.getAttributeResourceValue(
                    "http://schemas.android.com/apk/res/android", "id", NO_ID));

            setBackgroundResource(attrs.getAttributeResourceValue(
                    "http://schemas.android.com/apk/res/android", "background",
                    NO_ID));
        }

        mButton = (Button) findViewById(R.id.button);

        if (mButton == null) {
            if (attrs != null) {
                mButton = new Button(context, attrs);
            } else {
                mButton = new Button(context);
            }

            mButton.setId(NO_ID);
            mButton.setClickable(false);
            mButton.setDuplicateParentStateEnabled(true);
            this.addView(mButton);
        }
        mPreloader = (ProgressBar) findViewById(R.id.preloader);

        if (mPreloader == null) {
            mPreloader = (ProgressBar) LayoutInflater.from(context).inflate(
                    R.layout.progress_button_preloader, this, false);
            addView(mPreloader);
        }

        mPreloader.setVisibility(View.GONE);
        this.setClickable(true);
    }

    public void showPreloader() {
        this.mPreloaderShown = true;
        mButton.setVisibility(View.GONE);
        mPreloader.setVisibility(View.VISIBLE);
    }

    public void hidePreloader() {
        this.mPreloaderShown = false;
        mButton.setVisibility(View.VISIBLE);
        mPreloader.setVisibility(View.GONE);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        if (savedState.isPreloaderShown()) {
            showPreloader();
        } else {
            hidePreloader();
        }
        requestLayout();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable p = super.onSaveInstanceState();
        return new SavedState(p, mPreloaderShown);
    }

    public Button getButton() {
        return mButton;
    }

    public ProgressBar getPreloader() {
        return mPreloader;
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        // As we save our own instance state, ensure our children don't save and
        // restore their state as well.
        super.dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(
            SparseArray<Parcelable> container) {
        /**
         * See comment in
         * {@link #dispatchSaveInstanceState(android.util.SparseArray)}
         */
        super.dispatchThawSelfOnly(container);
    }

    /**
     * Convenience class to save / restore the lock combination picker state.
     * Looks clumsy but once created is easy to maintain and use.
     */
    protected static class SavedState extends BaseSavedState {

        private final boolean mPreloaderShown;

        private SavedState(Parcelable superState, boolean preloaderShown) {
            super(superState);
            this.mPreloaderShown = preloaderShown;
        }

        private SavedState(Parcel in) {
            super(in);
            mPreloaderShown = (Boolean) in.readValue(null);
        }

        public boolean isPreloaderShown() {
            return mPreloaderShown;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeValue(mPreloaderShown);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

    }
}
