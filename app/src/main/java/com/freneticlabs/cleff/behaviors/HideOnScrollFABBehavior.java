package com.freneticlabs.cleff.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.freneticlabs.cleff.utils.Utils;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Code taken from https://gist.github.com/mzgreen/7b77aa4ec95f81c3bed0#file-scrollingfabbehavior-java
 *
 * Allows the Floating Action Button to slide down off and up the screen when scrolling
 */
public class HideOnScrollFABBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {
    private int toolbarHeight;

    public HideOnScrollFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = Utils.getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionsMenu fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionsMenu fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ratio = (float)dependency.getY()/(float)toolbarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }
}