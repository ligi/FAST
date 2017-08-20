package org.ligi.fast.background;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.tracedroid.logging.Log;

import java.util.List;

/**
 * Async-Task to Retrieve / Store Application Info needed by this App
 */
public class BaseAppGatherAsyncTask extends AsyncTask<Void, AppInfo, Void> {
    private final Context ctx;
    protected int appCount;
    protected AppInfoList appInfoList;
    private final AppInfoList oldAppList;

    public BaseAppGatherAsyncTask(Context ctx) {
        this(ctx, null);
    }

    public BaseAppGatherAsyncTask(Context ctx, AppInfoList oldAppList) {
        this.ctx = ctx;
        appInfoList = new AppInfoList();
        this.oldAppList = oldAppList;
    }

    private void processCategory(final String category) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(category);
        try {
            final List<ResolveInfo> resolveInfoList = ctx.getPackageManager().queryIntentActivities(mainIntent, 0);
            appCount += resolveInfoList.size();
            for (ResolveInfo info : resolveInfoList) {
                final AppInfo actAppInfo = new AppInfo(ctx, info);

                if (!ctx.getPackageName().equals(actAppInfo.getPackageName())) { // ignore self

                    // Update call count from current index that is being used.
                    // This is because we may have updated the call count since the last time
                    // we saved the package list. An alternative would be to save the package list
                    // each time we leave
                    if (oldAppList != null) {
                        for(AppInfo oldInfo : appInfoList) {
                            if (oldInfo.getActivityName().equals(actAppInfo.getActivityName())) {
                                actAppInfo.setCallCount(oldInfo.getCallCount());
                                actAppInfo.setPinMode(oldInfo.getPinMode());
                                break;
                            }
                        }
                    }
                    appInfoList.add(actAppInfo);
                    publishProgress(actAppInfo);
                }
            }
        } catch (Exception e) {
            Log.d("Exception occurred when getting activities skipping...!");
        }

    }


    @Override
    protected Void doInBackground(Void... params) {
        // TODO the progressbar could be more exact here by first querying both - calculating the
        // total app-count and then process them - but as we do not expect that much launchers we
        // should be OK here
        appCount=0;
        processCategory(Intent.CATEGORY_LAUNCHER);
        processCategory(Intent.CATEGORY_HOME);
        return null;
    }


}
