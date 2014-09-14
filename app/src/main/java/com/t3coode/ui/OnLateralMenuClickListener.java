package com.t3coode.ui;

import android.view.View;

import com.t3coode.togg.activities.adapters.MenuAdapter.MenuItem;

public interface OnLateralMenuClickListener {

    boolean onMenuOptionClick(MenuItem menuItem, View view);

    boolean shouldSelectItem(int itemId);

}
