package me.buck.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 广播接收器的简单封装，默认接收本地广播
 */
public abstract class SimpleReceiver extends BroadcastReceiver {

    protected Context      mContext;
    protected boolean      mIsLocal = false;
    protected IntentFilter mFilter  = new IntentFilter();


    public SimpleReceiver(Context context) {
        mContext = context;
    }


    public void register() {
        if (mIsLocal) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(this, mFilter);
        } else {
            mContext.registerReceiver(this, mFilter);
        }
    }

    public void unregister() {
        if (mIsLocal) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);
        } else {
            mContext.unregisterReceiver(this);
        }
    }
}