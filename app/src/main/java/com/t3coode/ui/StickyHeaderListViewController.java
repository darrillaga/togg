package com.t3coode.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.t3coode.togg.R;

public class StickyHeaderListViewController implements OnScrollListener {

    private ListView mListView;
    private ViewGroup mStickyHolder;
    private LinearLayout mStickyHolderContent;
    private View mStickyView;
    private View mEmptyStickyView;
    private View mStickyConvertView;
    private boolean mInitialized;
    private StickyHeaderListViewAdapter mAdapter;

    private int mPrevFirstVisiblePosition = 0;
    private int mLastTop = 0;

    public StickyHeaderListViewController(ListView v, ViewGroup stickyHolder,
            View emptyStickyView, StickyHeaderListViewAdapter adapter) {

        // if (v.getAdapter() == null
        // || !(v.getAdapter() instanceof StickyHeaderListViewAdapter)) {
        // throw new RuntimeException(
        // "The list view's adapter must be an implementation of StickyHeaderListViewAdapter");
        // }

        this.mAdapter = adapter;
        this.mListView = v;
        this.mStickyHolder = stickyHolder;
        this.mStickyHolderContent = (LinearLayout) stickyHolder
                .findViewById(R.id.sticky_holder_content);
        this.mEmptyStickyView = emptyStickyView;

        initStickyController();
    }

    public void restartStickyController() {
        mStickyHolderContent.removeAllViews();
        if (mAdapter.getCount() > 0) {
            this.mInitialized = true;
            this.mStickyView = mAdapter.getView(0, mStickyConvertView,
                    mStickyHolderContent);

            mStickyView.setTag(0);
            mStickyHolderContent.addView(mStickyView);
            mStickyHolder.setClickable(true);

            mListView.setOnScrollListener(this);
        } else {
            mStickyHolderContent.addView(mEmptyStickyView);
        }
    }

    public void initStickyController() {
        if (!mInitialized) {
            restartStickyController();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView listView, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {

        int direction = firstVisibleItem - mPrevFirstVisiblePosition;
        mPrevFirstVisiblePosition = firstVisibleItem;

        if (listView.getChildCount() > 0) {

            int directionWithPrecission = mLastTop
                    - listView.getChildAt(0).getTop();
            mLastTop = listView.getChildAt(0).getTop();

            if (direction == 0) {
                direction = directionWithPrecission;
            }

            int position = -1;
            View v = null;

            if (direction <= 0) {
                position = mAdapter.getPrevHeaderViewPosition(firstVisibleItem);

                if (((Integer) mStickyView.getTag()) != position
                        && position != -1 && position < firstVisibleItem) {

                    View dummy = mListView.getChildAt(0);

                    if (dummy.getTop() <= mStickyHolder.getBottom()) {

                        v = mListView.getAdapter().getView(position,
                                mStickyConvertView, mStickyHolderContent);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStickyHolder
                                .getLayoutParams();

                        params.topMargin = -dummy.getHeight();

                        mStickyHolder.setLayoutParams(params);

                    }
                }

                if (mListView.getAdapter()
                        .getItemViewType(firstVisibleItem + 1) == StickyHeaderListViewAdapter.VIEW_TYPE_HEADER) {
                    View firstView = listView.getChildAt(0);

                    // if (firstView.getTop() <=
                    // mStickyView.getBottom()) {

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStickyHolder
                            .getLayoutParams();

                    int top = firstView.getTop();
                    int height = firstView.getHeight()
                            - mStickyHolder.getHeight();

                    params.topMargin = top + height;

                    if (params.topMargin > 0) {
                        params.topMargin = 0;
                    }

                    mStickyHolder.setLayoutParams(params);
                    // }
                } else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStickyHolder
                            .getLayoutParams();
                    if (params.topMargin != 0) {
                        params.topMargin = 0;
                        mStickyHolder.setLayoutParams(params);
                    }
                }
            } else {
                position = mAdapter.getNextHeaderViewPosition(firstVisibleItem);

                boolean isNextHeader = mListView.getAdapter().getItemViewType(
                        firstVisibleItem + 1) == StickyHeaderListViewAdapter.VIEW_TYPE_HEADER;

                if (((Integer) mStickyView.getTag()) != position
                        && position != -1 && position < firstVisibleItem + 1) {

                    View dummy = mListView.getChildAt(position
                            - firstVisibleItem);

                    if ((mStickyView == null || (dummy.getTop() <= mStickyHolder
                            .getBottom()))) {
                        v = mListView.getAdapter().getView(position,
                                mStickyConvertView, mStickyHolderContent);
                    }
                } else if (!isNextHeader) {
                    position = mAdapter
                            .getPrevHeaderViewPosition(firstVisibleItem);

                    v = mListView.getAdapter().getView(position,
                            mStickyConvertView, mStickyHolderContent);
                }

                if (isNextHeader) {
                    View firstView = listView.getChildAt(1);

                    if (firstView.getTop() < mStickyHolder.getBottom()) {

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStickyHolder
                                .getLayoutParams();

                        params.topMargin = -(mStickyHolder.getHeight() - firstView
                                .getTop());

                        mStickyHolder.setLayoutParams(params);
                    }
                } else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStickyHolder
                            .getLayoutParams();
                    if (params.topMargin != 0) {
                        params.topMargin = 0;
                        mStickyHolder.setLayoutParams(params);
                    }
                }
            }

            if (v != null) {
                if (mStickyHolderContent.getChildCount() > 0) {
                    mStickyHolderContent.removeAllViews();
                }

                mStickyView = v;
                mStickyHolderContent.addView(v);
                v.setTag(position);
            }
        }
    }
}
