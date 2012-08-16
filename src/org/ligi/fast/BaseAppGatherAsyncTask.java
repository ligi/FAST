package org.ligi.fast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
/**
 * Async-Task to Retrieve / Store Application Info needed by this App
 * 
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 */
public class BaseAppGatherAsyncTask extends AsyncTask<Void, AppInfo, Void> {
	private Context ctx;
	
	public BaseAppGatherAsyncTask(Context ctx) {
		this.ctx=ctx;
	}

	@Override
	protected Void doInBackground(Void... params) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		for (ResolveInfo info : ctx.getPackageManager().queryIntentActivities(mainIntent, 0)) {
			AppInfo act_appinfo=new AppInfo(ctx,info);
			publishProgress(act_appinfo);
		}
		
		return null;
	}
}
