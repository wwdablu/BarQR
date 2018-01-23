package com.soumya.wwdablu.barqr;

import android.util.Log;

import timber.log.Timber;

public class ReleaseTree extends Timber.Tree {

    @Override
    protected boolean isLoggable(String tag, int priority) {

        switch (priority) {

            case Log.ASSERT:
            case Log.VERBOSE:
            case Log.DEBUG:
            case Log.INFO:
                return false;

            default:
                return super.isLoggable(tag, priority);
        }
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        switch (priority) {

            case Log.ERROR:
                Log.e(tag, message, t);
                break;

            default:
                //Do nothing
        }
    }
}
