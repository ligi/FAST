package org.ligi.fast.background;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import org.ligi.fast.App;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.PackageManagerUtils;

import java.util.List;

/**
 * Async-Task to Retrieve / Store Application Info needed by this App
 */
public class BaseAppGatherAsyncTask extends AsyncTask<Void, AppInfo, Void> {
    protected Context mContext;
    protected final AppInfoList appInfoList = new AppInfoList();
    protected final List<String> mChangedPackageNames;
    protected int appCount;

    public BaseAppGatherAsyncTask(Context ctx) {
        this(ctx, null);
    }

    public BaseAppGatherAsyncTask(Context context, List<String> changedPackages) {
        this.mContext = context.getApplicationContext();
        this.mChangedPackageNames = changedPackages;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<ResolveInfo> activitiesResolveInfoList = PackageManagerUtils.getResolveInfo(mContext, mChangedPackageNames);
        appCount = activitiesResolveInfoList.size();
        for (ResolveInfo info : activitiesResolveInfoList) {
            AppInfo newAppInfo = new AppInfo(mContext, info);
            appInfoList.add(newAppInfo);
            publishProgress(newAppInfo);
        }
        mContext = null;
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        App.getPackageChangedListener().onPackageChange(mChangedPackageNames, appInfoList);
        //TODO trim icon cache
    }
}
