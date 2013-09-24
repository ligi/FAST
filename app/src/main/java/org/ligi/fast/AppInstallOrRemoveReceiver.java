package org.ligi.fast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ligi.tracedroid.logging.Log;

public class AppInstallOrRemoveReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("Broadcast rcv" + intent.getAction() + " " + intent.getData());

        if (null != App.packageChangedListener) {
            App.packageChangedListener.onPackageChange();
        }
    }
}
