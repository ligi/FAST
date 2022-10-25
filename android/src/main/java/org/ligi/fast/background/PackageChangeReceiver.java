package org.ligi.fast.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;

import org.ligi.fast.App;

import java.util.Collections;

public class PackageChangeReceiver extends BroadcastReceiver {
    public void register(Context context) {
        if (Build.VERSION.SDK_INT >= 7) {
            context.registerReceiver(this, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        if (data == null) return; // This should never be the case
        String packageName = data.getSchemeSpecificPart();
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_LOCALE_CHANGED:
                new BaseAppGatherAsyncTask(context).execute();
                break;
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_CHANGED:
                new BaseAppGatherAsyncTask(context, Collections.singletonList(packageName)).execute();
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                // During an upgrade there will be a PACKAGE_REMOVED, then a PACKAGE_ADDED Broadcast.
                // defaultValue is false because this extra does not exist during full uninstallation.
                //TODO: On MinApiLevel 14: Replace Intent.ACTION_PACKAGE_REMOVED with Intent.ACTION_PACKAGE_FULLY_REMOVED
                if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    App.getPackageChangedListener().onPackageChange(Collections.singletonList(packageName), null);
                }
                //TODO trim icon cache
                break;
        }
    }
}
