package org.ligi.fast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;

/**
 * Async-Task to Retrieve / Store Application Info needed by this App
 */
class BaseAppGatherAsyncTask extends AsyncTask<Void, AppInfo, Void> {
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
                
                pkgnames.add(info.activityInfo.packageName.toString());
                
            }
        } catch (Exception e) {
            Log.d("Exception occurred when getting activities skipping...!");
        }
        
        //Remove duplicates... 
        LinkedHashSet<String>  pkgnames_hs = new LinkedHashSet<String> ();
        pkgnames_hs.addAll(pkgnames);
        pkgnames.clear();
        pkgnames.addAll(pkgnames_hs);
        
        //Create appinfo with new implemetation by talexop
        for (String appPkgName : pkgnames) {
        	AppInfo act_appinfoStr = new AppInfo(ctx,appPkgName,true);
        	publishProgress(act_appinfoStr);
        }
        
        return null;
    }


}
