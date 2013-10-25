package org.ligi.fast.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppInstallOrRemoveReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        new BackgroundGatherAsyncTask(context).execute();
    }
}
