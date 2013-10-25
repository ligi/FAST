package org.ligi.fast.background;

import android.content.Context;

import org.ligi.fast.App;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.util.PackageListStore;

public class BackgroundGatherAsyncTask extends BaseAppGatherAsyncTask {

    private Context context;

    public BackgroundGatherAsyncTask(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onProgressUpdate(AppInfo... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        new PackageListStore(context).save(appInfoList);
        if (App.packageChangedListener != null) {
            App.packageChangedListener.onPackageChange();
        }
    }

}
