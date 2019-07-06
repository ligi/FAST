package org.ligi.fast.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ligi.fast.App;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.AppInfoListStore;

/**
 * Refreshes the whole AppInfoList to update labels when the user changes the system language
 */
public class LocaleChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new BackgroundGatherAsyncTask(context.getApplicationContext()).execute();
    }
}
