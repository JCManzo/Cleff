package com.freneticlabs.cleff.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by jcmanzo on 1/3/15.
 */
public class CheckableLayout extends LinearLayout implements
    Checkable {

    private boolean mIsChecked;
    private CheckableImageButton mCheckableImageButton;
    private LinearLayout mLinearLayout;
    public CheckableLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CheckableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CheckableLayout (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        return mCheckableImageButton != null && mCheckableImageButton.isChecked();
    }

    @Override
    public void toggle() {
        if(mCheckableImageButton != null) {
            mCheckableImageButton.toggle();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if(mCheckableImageButton != null) {
            mCheckableImageButton.setChecked(checked);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final int childCount = getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if(v instanceof CheckableImageButton) {
                mCheckableImageButton = (CheckableImageButton)v;
            }
        }
    }
}
