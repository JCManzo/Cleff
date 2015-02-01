package com.freneticlabs.cleff.models.events;

import android.database.Cursor;

/**
 * Created by jcmanzo on 1/29/15.
 */
public class QueryAllSongsEvent {
    public final Cursor mCursor;

    public QueryAllSongsEvent(Cursor cursor) {
        mCursor = cursor;
    }
}
