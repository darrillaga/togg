package com.t3coode.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.t3coode.togg.R;

public class ProgressImageButton extends FrameLayout implements ProgressView {

    private ImageButton mButton;
    private ProgressBar mPreloader;

    public ProgressImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        initContent(context, attrs);
    }

    public ProgressImageButton(Context context, AttributeSet attrs) {
        super(context);
        initContent(context, attrs);
    }

    public ProgressImageButton(Context context) {
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

        mButton = (ImageButton) findViewById(R.id.button);

        if (mButton == null) {
            if (attrs != null) {
                mButton = new ImageButton(context, attrs);
            } else {
                mButton = new ImageButton(context);
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
        mButton.setVisibility(View.GONE);
        mPreloader.setVisibility(View.VISIBLE);
        setSelected(true);
    }

    public void hidePreloader() {
        mButton.setVisibility(View.VISIBLE);
        mPreloader.setVisibility(View.GONE);
        setSelected(false);
    }

    public ImageButton getButton() {
        return mButton;
    }

    public ProgressBar getPreloader() {
        return mPreloader;
    }
}
