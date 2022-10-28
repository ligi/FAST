package org.ligi.fast.background;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;

import org.ligi.fast.App;
import org.ligi.fast.model.AppIconCache;
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
    protected List<String> mChangedPackageNames;
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
        invalidateCacheIfIconMaskChanged();
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

    /**
     * Check if the adaptive icons shape that was last used to generate the icon cache still applies
     * If it changed, invalidate the icon cache and record the current shape.
     */
    private void invalidateCacheIfIconMaskChanged() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Resources r = mContext.getPackageManager().getResourcesForApplication("android");
                int resId = r.getIdentifier("config_icon_mask", "string", "android");
                if (resId != 0) {
                    String mask = r.getString(resId);
                    if (mask.equals(App.getSettings().getLastIconShape())) {
                        return;
                    }
                    AppIconCache.invalidateIconCache();
                    mChangedPackageNames = null;
                    App.getSettings().putLastIconShape(mask);
                }
            } catch (PackageManager.NameNotFoundException e) { /*no android package, help*/
            }
        }
    }
}
