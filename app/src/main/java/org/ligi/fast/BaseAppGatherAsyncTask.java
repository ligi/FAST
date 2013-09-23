package org.ligi.fast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import org.ligi.tracedroid.logging.Log;

import java.util.List;

/**
 * Async-Task to Retrieve / Store Application Info needed by this App
 */
class BaseAppGatherAsyncTask extends AsyncTask<Void, AppInfo, Void> {
    private final Context ctx;
    int appCount;

    public BaseAppGatherAsyncTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        try {
            List<ResolveInfo> resolveInfoList = ctx.getPackageManager().queryIntentActivities(mainIntent, 0);
            appCount = resolveInfoList.size();
            for (ResolveInfo info : resolveInfoList) {
                AppInfo act_appinfo = new AppInfo(ctx, info);

                if (!ctx.getPackageName().equals(act_appinfo.getPackageName())) {
                    // do not add this app as app in list
                    publishProgress(act_appinfo);
                }
            }
        } catch (Exception e) {
            Log.d("Exception occurred when getting activities skipping...!");
        }

        return null;
    }
}
