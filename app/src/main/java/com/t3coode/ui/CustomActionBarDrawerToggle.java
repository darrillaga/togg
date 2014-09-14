package com.t3coode.ui;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.t3coode.togg.R;

public class CustomActionBarDrawerToggle implements
        android.support.v4.widget.DrawerLayout.DrawerListener {
    private static class SlideDrawable extends Drawable implements
            android.graphics.drawable.Drawable.Callback {

        public void setOffset(float offset) {
            mOffset = offset;
            invalidateSelf();
        }

        public float getOffset() {
            return mOffset;
        }

        public void setOffsetBy(float offsetBy) {
            mOffsetBy = offsetBy;
            invalidateSelf();
        }

        public void draw(Canvas canvas) {
            mWrapped.copyBounds(mTmpRect);
            canvas.save();
            canvas.translate(mOffsetBy * (float) mTmpRect.width() * -mOffset,
                    0.0F);
            mWrapped.draw(canvas);
            canvas.restore();
        }

        public void setChangingConfigurations(int configs) {
            mWrapped.setChangingConfigurations(configs);
        }

        public int getChangingConfigurations() {
            return mWrapped.getChangingConfigurations();
        }

        public void setDither(boolean dither) {
            mWrapped.setDither(dither);
        }

        public void setFilterBitmap(boolean filter) {
            mWrapped.setFilterBitmap(filter);
        }

        public void setAlpha(int alpha) {
            mWrapped.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            mWrapped.setColorFilter(cf);
        }

        public void setColorFilter(int color,
                android.graphics.PorterDuff.Mode mode) {
            mWrapped.setColorFilter(color, mode);
        }

        public void clearColorFilter() {
            mWrapped.clearColorFilter();
        }

        public boolean isStateful() {
            return mWrapped.isStateful();
        }

        public boolean setState(int stateSet[]) {
            return mWrapped.setState(stateSet);
        }

        public int[] getState() {
            return mWrapped.getState();
        }

        public Drawable getCurrent() {
            return mWrapped.getCurrent();
        }

        public boolean setVisible(boolean visible, boolean restart) {
            return super.setVisible(visible, restart);
        }

        public int getOpacity() {
            return mWrapped.getOpacity();
        }

        public Region getTransparentRegion() {
            return mWrapped.getTransparentRegion();
        }

        protected boolean onStateChange(int state[]) {
            mWrapped.setState(state);
            return super.onStateChange(state);
        }

        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mWrapped.setBounds(bounds);
        }

        public int getIntrinsicWidth() {
            return mWrapped.getIntrinsicWidth();
        }

        public int getIntrinsicHeight() {
            return mWrapped.getIntrinsicHeight();
        }

        public int getMinimumWidth() {
            return mWrapped.getMinimumWidth();
        }

        public int getMinimumHeight() {
            return mWrapped.getMinimumHeight();
        }

        public boolean getPadding(Rect padding) {
            return mWrapped.getPadding(padding);
        }

        public android.graphics.drawable.Drawable.ConstantState getConstantState() {
            return super.getConstantState();
        }

        public void invalidateDrawable(Drawable who) {
            if (who == mWrapped)
                invalidateSelf();
        }

        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            if (who == mWrapped)
                scheduleSelf(what, when);
        }

        public void unscheduleDrawable(Drawable who, Runnable what) {
            if (who == mWrapped)
                unscheduleSelf(what);
        }

        private Drawable mWrapped;
        private float mOffset;
        private float mOffsetBy;
        private final Rect mTmpRect = new Rect();

        public SlideDrawable(Drawable wrapped) {
            mWrapped = wrapped;
        }
    }

    private static class ActionBarDrawerToggleImplBase implements
            ActionBarDrawerToggleImpl {

        public Drawable getThemeUpIndicator(ActionBarActivity activity) {
            return ((ImageView) activity.getSupportActionBar().getCustomView()
                    .findViewById(R.id.action_bar_up_button)).getDrawable();
        }

        public Object setActionBarUpIndicator(Object info,
                ActionBarActivity activity, Drawable themeImage, int i) {

            ImageView upButton = ((ImageView) activity.getSupportActionBar()
                    .getCustomView().findViewById(R.id.action_bar_up_button));
            upButton.setImageDrawable(themeImage);
            upButton.setVisibility(View.VISIBLE);
            return info;
        }

        public Object setActionBarDescription(Object info,
                ActionBarActivity activity, int contentDescRes) {
            return info;
        }

        private ActionBarDrawerToggleImplBase() {
        }

    }

    private static interface ActionBarDrawerToggleImpl {

        public abstract Drawable getThemeUpIndicator(ActionBarActivity activity);

        public abstract Object setActionBarUpIndicator(Object obj,
                ActionBarActivity activity, Drawable drawable, int i);

        public abstract Object setActionBarDescription(Object obj,
                ActionBarActivity activity, int i);
    }

    public CustomActionBarDrawerToggle(ActionBarActivity activity,
            DrawerLayout drawerLayout, int drawerImageRes,
            int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        mDrawerIndicatorEnabled = true;
        mActivity = activity;
        mDrawerLayout = drawerLayout;
        mDrawerImageResource = drawerImageRes;
        mOpenDrawerContentDescRes = openDrawerContentDescRes;
        mCloseDrawerContentDescRes = closeDrawerContentDescRes;
        mThemeImage = IMPL.getThemeUpIndicator(activity);
        mDrawerImage = activity.getResources().getDrawable(drawerImageRes);
        mSlider = new SlideDrawable(mDrawerImage);
        mSlider.setOffsetBy(0.3333333F);

        activity.getSupportActionBar().getCustomView()
                .findViewById(R.id.action_bar_home)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        toggleDrawer();
                    }
                });

    }

    public void syncState() {
        if (mDrawerLayout.isDrawerOpen(8388611))
            mSlider.setOffset(1.0F);
        else
            mSlider.setOffset(0.0F);
        if (mDrawerIndicatorEnabled)
            mSetIndicatorInfo = IMPL
                    .setActionBarUpIndicator(
                            mSetIndicatorInfo,
                            mActivity,
                            mSlider,
                            mDrawerLayout.isDrawerOpen(8388611) ? mOpenDrawerContentDescRes
                                    : mCloseDrawerContentDescRes);
    }

    public void setDrawerIndicatorEnabled(boolean enable) {
        if (enable != mDrawerIndicatorEnabled) {
            if (enable)
                mSetIndicatorInfo = IMPL
                        .setActionBarUpIndicator(
                                mSetIndicatorInfo,
                                mActivity,
                                mSlider,
                                mDrawerLayout.isDrawerOpen(8388611) ? mOpenDrawerContentDescRes
                                        : mCloseDrawerContentDescRes);
            else
                mSetIndicatorInfo = IMPL.setActionBarUpIndicator(
                        mSetIndicatorInfo, mActivity, mThemeImage, 0);
            mDrawerIndicatorEnabled = enable;
        }
    }

    public boolean isDrawerIndicatorEnabled() {
        return mDrawerIndicatorEnabled;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        mThemeImage = IMPL.getThemeUpIndicator(mActivity);
        mDrawerImage = mActivity.getResources().getDrawable(
                mDrawerImageResource);
        syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == 16908332
                && mDrawerIndicatorEnabled)
            toggleDrawer();
        return false;
    }

    public void toggleDrawer() {
        if (mDrawerLayout.isDrawerVisible(8388611))
            mDrawerLayout.closeDrawer(8388611);
        else
            mDrawerLayout.openDrawer(8388611);
    }

    public void onDrawerSlide(View drawerView, float slideOffset) {
        float glyphOffset = mSlider.getOffset();
        if (slideOffset > 0.5F)
            glyphOffset = Math.max(glyphOffset,
                    Math.max(0.0F, slideOffset - 0.5F) * 2.0F);
        else
            glyphOffset = Math.min(glyphOffset, slideOffset * 2.0F);
        mSlider.setOffset(glyphOffset);
    }

    public void onDrawerOpened(View drawerView) {
        mSlider.setOffset(1.0F);
        if (mDrawerIndicatorEnabled)
            mSetIndicatorInfo = IMPL.setActionBarDescription(mSetIndicatorInfo,
                    mActivity, mOpenDrawerContentDescRes);
    }

    public void onDrawerClosed(View drawerView) {
        mSlider.setOffset(0.0F);
        if (mDrawerIndicatorEnabled)
            mSetIndicatorInfo = IMPL.setActionBarDescription(mSetIndicatorInfo,
                    mActivity, mCloseDrawerContentDescRes);
    }

    public void onDrawerStateChanged(int i) {
    }

    private static final ActionBarDrawerToggleImpl IMPL;
    private static final int ID_HOME = 16908332;
    private final ActionBarActivity mActivity;
    private final DrawerLayout mDrawerLayout;
    private boolean mDrawerIndicatorEnabled;
    private Drawable mThemeImage;
    private Drawable mDrawerImage;
    private SlideDrawable mSlider;
    private final int mDrawerImageResource;
    private final int mOpenDrawerContentDescRes;
    private final int mCloseDrawerContentDescRes;
    private Object mSetIndicatorInfo;

    static {
        IMPL = new ActionBarDrawerToggleImplBase();
    }
}
