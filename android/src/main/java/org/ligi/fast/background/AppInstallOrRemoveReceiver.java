package org.ligi.fast.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.ligi.fast.App;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.AppInfoListStore;

public class AppInstallOrRemoveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final AppInfoListStore appInfoListStore = new AppInfoListStore(context);

        if (App.packageChangedListener == null) {
            App.packageChangedListener = new App.PackageChangedListener() {
                @Override
                public void onPackageChange(AppInfoList appInfoList) {
                    appInfoListStore.save(appInfoList);
                }
            };
        }

        new BackgroundGatherAsyncTask(context, appInfoListStore.load()).execute();
    }
}
