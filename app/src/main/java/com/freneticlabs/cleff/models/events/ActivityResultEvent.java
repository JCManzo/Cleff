package com.freneticlabs.cleff.models.events;

import android.content.Intent;

/**
 * ActivityResultEvent object to be used when startActivityForResult is
 * called from within a nested fragment. Android OS will only call
 * the first levels fragment's onActivityResult() and ignore any
 * nested fragments. This is a known bug in the android architecture
 * and this is currently the workaround for that.
 *
 *  Created on 8/10/15 with code taken from
 *  http://inthecheesefactory.com/blog/how-to-fix-nested-fragment-onactivityresult-issue/en
 */
public class ActivityResultEvent {
    private int requestCode;
    private int resultCode;
    private Intent data;

    public ActivityResultEvent(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }
}
