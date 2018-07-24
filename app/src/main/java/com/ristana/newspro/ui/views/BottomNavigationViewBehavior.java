package com.ristana.newspro.ui.views;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by hsn on 21/12/2017.
 */

public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationViewEx> {

    private int height;

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigationViewEx child, int layoutDirection) {
        height = child.getHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, BottomNavigationViewEx child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, BottomNavigationViewEx child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyConsumed > 0) {
            slideDown(child);
        } else if (dyConsumed < 0) {
            slideUp(child);
        }
    }

    private void slideUp(BottomNavigationViewEx child) {
        child.clearAnimation();
        child.animate().translationY(0).setDuration(200);
    }

    private void slideDown(BottomNavigationViewEx child) {
        child.clearAnimation();
        child.animate().translationY(height).setDuration(200);
    }
}