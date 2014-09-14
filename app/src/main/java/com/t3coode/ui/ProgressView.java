package com.t3coode.ui;

import android.widget.ProgressBar;

public interface ProgressView {

    public void showPreloader();

    public void hidePreloader();

    public ProgressBar getPreloader();
}
