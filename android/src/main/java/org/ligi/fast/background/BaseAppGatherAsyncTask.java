package org.ligi.fast.background;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.ligi.fast.App;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.AppInfoListStore;

import java.util.Iterator;
import java.util.List;

/**
 * Async-Task to Retrieve / Store Application Info needed by this App
 */
public class BaseAppGatherAsyncTask extends AsyncTask<Void, AppInfo, Void> {
    private Context ctx;
    private AppInfoList oldAppList;
    protected int appCount;
    protected AppInfoList appInfoList;

    protected BaseAppGatherAsyncTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(Void... params) {
        long start = System.currentTimeMillis();
        long start_total = System.currentTimeMillis();
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = ctx.getPackageManager().queryIntentActivities(launcherIntent, 0);

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> homeResolveInfoList = ctx.getPackageManager().queryIntentActivities(homeIntent, 0);
        // Deduplicate Resolve Info of activities with both categories - like SearchActivity (see manifest)
        for (ResolveInfo info : resolveInfoList) {
            Iterator<ResolveInfo> homeIterator = homeResolveInfoList.iterator();
            while (homeIterator.hasNext()) {
                ResolveInfo homeInfo = homeIterator.next();
                if (homeInfo.activityInfo.name.equals(info.activityInfo.name)) {
                    homeIterator.remove();
                    break;
                }
            }
            if (!homeIterator.hasNext()) {
                break;
            }
        }
        resolveInfoList.addAll(homeResolveInfoList);

        long now = System.currentTimeMillis();
        long duration = now - start;
        Log.d(App.LOG_TAG, "AppList query ran " + duration + "ms.");

        start = System.currentTimeMillis();
        if (App.backingAppInfoList != null) {
            this.oldAppList = new AppInfoList();
            this.oldAppList.addAll(App.backingAppInfoList.get());
        } else {
            this.oldAppList = new AppInfoListStore(ctx).load();
        }
        appInfoList = new AppInfoList();
        appCount = resolveInfoList.size();
        now = System.currentTimeMillis();
        duration = now - start;
        Log.d(App.LOG_TAG, "AppList loading ran " + duration + "ms.");

        start = System.currentTimeMillis();
        for (ResolveInfo info : resolveInfoList) {
            // ignore self and Android Settings FallbackHome
            if (!ctx.getPackageName().equals(info.activityInfo.packageName)
                    && !"com.android.settings.FallbackHome".equals(info.activityInfo.name)) {
                AppInfo newAppInfo = new AppInfo(ctx, info);

                // Update call count from current index that is being used.
                // This is because we may have updated the call count since the last time
                // we saved the package list. An alternative would be to save the package list
                // each time we leave
                if (oldAppList != null) {
                    Iterator<AppInfo> oldInfoIterator = oldAppList.iterator();
                    while (oldInfoIterator.hasNext()) {
                        AppInfo oldInfo = oldInfoIterator.next();
                        if (oldInfo.getActivityName().equals(newAppInfo.getActivityName())) {
                            if (oldInfo.getLabelMode() == 2) { // AppInfo is alias
                                oldInfo.setLabel(newAppInfo.getLabel());
                                oldInfo.setInstallTime(newAppInfo.getInstallTime());
                                appInfoList.add(oldInfo);
                            } else {
                                newAppInfo.setCallCount(oldInfo.getCallCount());
                                newAppInfo.setPinMode(oldInfo.getPinMode());
                                newAppInfo.setLabelMode(oldInfo.getLabelMode());
                                newAppInfo.setOverrideLabel(oldInfo.getOverrideLabel());
                            }
                            oldInfoIterator.remove();
                            // Can't break here anymore because of aliases
                            // So instead this removes entries after processing
                        }
                    }
                }
                appInfoList.add(newAppInfo);
                publishProgress(newAppInfo);
            }
        }
        now = System.currentTimeMillis();
        duration = now - start;
        Log.d(App.LOG_TAG, "AppList merging ran " + duration + "ms.");

        ctx = null;
        long end = System.currentTimeMillis();
        duration = end - start_total;
        Log.d(App.LOG_TAG, "Full Update ran " + duration + "ms.");
        return null;
    }


}
