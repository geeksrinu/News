package com.ristana.newspro.ui.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by hsn on 24/02/2018.
 */

public class ILinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public ILinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
