package org.ligi.fast.background;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import org.ligi.fast.model.AppInfo;
import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Async-Task to Retrieve / Store Application Info needed by this App
 */
public class BaseAppGatherAsyncTask extends AsyncTask<Void, AppInfo, Void> {
    private final Context ctx;
    protected int appCount;
    protected List<AppInfo> appInfoList;

    public BaseAppGatherAsyncTask(Context ctx) {
        this.ctx = ctx;
        appInfoList = new ArrayList<AppInfo>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        try {
            List<ResolveInfo> resolveInfoList = ctx.getPackageManager().queryIntentActivities(mainIntent, 0);
            appCount = resolveInfoList.size();
            for (ResolveInfo info : resolveInfoList) {
                AppInfo actAppInfo = new AppInfo(ctx, info);

                if (!ctx.getPackageName().equals(actAppInfo.getPackageName())) { // ignore self
                    appInfoList.add(actAppInfo);
                    publishProgress(actAppInfo);
                }
            }
        } catch (Exception e) {
            Log.d("Exception occurred when getting activities skipping...!");
        }

        return null;
    }


}
