package org.ligi.fast.background;

import android.content.Context;

import org.ligi.fast.App;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.AppInfoListStore;

import java.util.List;

public class BackgroundGatherAsyncTask extends BaseAppGatherAsyncTask {

    public BackgroundGatherAsyncTask(Context context, AppInfoList oldAppInfoList) {
        super(context, oldAppInfoList);
    }

    @Override
    protected void onProgressUpdate(AppInfo... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (App.packageChangedListener != null) {
            App.packageChangedListener.onPackageChange(appInfoList);
        }
    }

}
