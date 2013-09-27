package org.ligi.fast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;
import java.util.List;

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
        
        List<List<String>> apps = new ArrayList<List<String>>();
        
        
        try {
        	
            List<ResolveInfo> resolveInfos = ctx.getPackageManager().queryIntentActivities(mainIntent, 0);
            appCount = resolveInfos.size();
              
            for (ResolveInfo info : resolveInfos) {
            	
            	ArrayList<String> data = new ArrayList<String>();
            	data.add(info.activityInfo.packageName.toString());
            	data.add(info.activityInfo.name.toString());
            	apps.add(data);
            }
            
            
        } catch (Exception e) {
            Log.d("Exception occurred when getting activities skipping...!");

        }
        
        
        for (int n = 0; n < apps.size(); n++) {
        	AppInfo act_appinfoStr = new AppInfo(ctx,apps.get(n).get(0).toString(),apps.get(n).get(1).toString());
            publishProgress(act_appinfoStr);
        }

        return null;
    }


}
