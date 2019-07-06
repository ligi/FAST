package org.ligi.fast.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.ligi.fast.App;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.AppInfoListStore;

import java.util.Iterator;
import java.util.List;

/**
 * Whenever an app is installed, uninstalled or components change
 * (e.g. the app disabled one of it's activities to hide it from the launcher)
 * this receiver takes care of removing or updating corresponding entries
 * (namely all activities and aliases) from AppInfoList and deletes their icons
 * from cache to clean up when uninstalling or to cause a refresh when updating.
 */
public class AppInstallOrRemoveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < 11) {
            new AppInstallOrRemoveAsyncTask(context.getApplicationContext(), intent).doInBackground(new Void[1]);
        } else {
            final PendingResult pendingResult = goAsync();
            new AppInstallOrRemoveAsyncTask(context.getApplicationContext(), intent, pendingResult).execute();
        }
    }

    private static class AppInstallOrRemoveAsyncTask extends AsyncTask<Void, AppInfo, Void> {
        private BroadcastReceiver.PendingResult mPendingResult;
        private Context mContext;
        private Intent mIntent;
        private AppInfoListStore mAppInfoListStore;

        private void save(AppInfoList appInfoList) {
            if (App.packageChangedListener == null) {
                if (App.backingAppInfoList != null) {
                    AppInfoList backingList = App.backingAppInfoList.get();
                    backingList.clear();
                    backingList.addAll(appInfoList);
                } else {
                    mAppInfoListStore.save(appInfoList);
                }
            } else {
                App.packageChangedListener.onPackageChange(appInfoList);
            }
        }

        AppInstallOrRemoveAsyncTask(Context context, Intent intent) {
            this.mContext = context;
            this.mIntent = intent;
        }

        AppInstallOrRemoveAsyncTask(Context context, Intent intent, BroadcastReceiver.PendingResult pendingResult) {
            this(context, intent);
            this.mPendingResult = pendingResult;
        }

        @Override
        protected Void doInBackground(Void... params) {
            long start = System.currentTimeMillis();
            Uri data = mIntent.getData();
            if (data == null) return null; // This should never be the case
            String packageName = data.getSchemeSpecificPart();
            String action = mIntent.getAction();

            Log.d(App.LOG_TAG, "BroadcastReceiver: Action - " + action + "; Package - " + packageName);

            //TODO: When moving to MinApiLevel 14 or higher, refactor Intent.ACTION_PACKAGE_REMOVED
            // to Intent.ACTION_PACKAGE_FULLY_REMOVED and remove the following block

            // If this is the removal Broadcast during an upgrade don't to do anything.
            // Data will be updated in the next invocation by the package added Broadcast.
            // This prevents running the update twice.
            // getBooleanExtra defaultValue is false so that this is not also tripped
            // in case of a full uninstall where this extra is missing.
            // NOTE: This is only due to compatibility below API level 14
            // When using the Intent.ACTION_PACKAGE_FULLY_REMOVED instead of
            // Intent.ACTION_PACKAGE_REMOVED this is not necessary
            boolean replacingDefaultFalse = mIntent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
            if (Intent.ACTION_PACKAGE_REMOVED.equals(action) && replacingDefaultFalse) return null;

            mAppInfoListStore = new AppInfoListStore(mContext);
            AppInfoList appInfoList;
            if (App.backingAppInfoList != null) {
                appInfoList = new AppInfoList();
                appInfoList.addAll(App.backingAppInfoList.get());
                if (appInfoList.size() == 0) {
                    appInfoList = mAppInfoListStore.load();
                }
            } else {
                appInfoList = mAppInfoListStore.load();
            }

            // Check the package is newly installed or not
            // getBooleanExtra defaultValue is true so that in case of doubt the
            // presence of old information is checked anyway
            boolean replacingDefaultTrue = mIntent.getBooleanExtra(Intent.EXTRA_REPLACING, true);
            boolean newInstall = Intent.ACTION_PACKAGE_ADDED.equals(action) && !replacingDefaultTrue;
            AppInfoList matchedAppInfoList = new AppInfoList();
            // If this is not a new install, i.e. update or uninstall, then collect the existing records
            // already held about this app into matchedAppInfoList and remove them from the main list.
            // That way if uninstalling an app the main list is already updated after this step.
            // If it is an update only matchedAppInfoList will have to be iterated in the next step
            // since it contains all records that need an update.
            // After this all old info & icons of the affected package should be cleaned up.
            if (!newInstall) {
                for (Iterator<AppInfo> iterator = appInfoList.iterator(); iterator.hasNext(); ) {
                    AppInfo appInfo = iterator.next();
                    if (appInfo.getPackageName().equals(packageName)) {
                        matchedAppInfoList.add(appInfo);
                        iterator.remove();
                        // Delete the current icon to force a refresh
                        //File icon = appInfo.getIconCacheFile();
                        //icon.delete();
                    }
                }
                // Just to be sure; If this is the case there is no old information
                // to update and new information can simply be added in the next step
                newInstall = matchedAppInfoList.size() == 0;
            }

            // If not uninstalled go on to update/add new information
            // Otherwise everything is done and the list just has to be saved
            if (!Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
                launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                launcherIntent.setPackage(packageName);
                List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(launcherIntent, 0);

                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setPackage(packageName);
                List<ResolveInfo> homeInfoList = mContext.getPackageManager().queryIntentActivities(homeIntent, 0);

                // If there are no activities that should be displayed on the launcher we can quit here
                if (resolveInfoList.size() == 0 && homeInfoList.size() == 0) {
                    save(appInfoList);
                    long end = System.currentTimeMillis();
                    long duration = end - start;
                    Log.d(App.LOG_TAG, "BroadcastReceiver ran short " + duration + "ms.");
                    return null;
                }

                // Deduplicate Resolve Info of activities with both categories - like SearchActivity (see manifest)
                for (ResolveInfo info : resolveInfoList) {
                    Iterator<ResolveInfo> homeIterator = homeInfoList.iterator();
                    while (homeIterator.hasNext()) {
                        ResolveInfo homeInfo = homeIterator.next();
                        if (homeInfo.activityInfo.name.equals(info.activityInfo.name)) {
                            homeIterator.remove();
                            break;
                        }
                    }
                    if (!homeIterator.hasNext()) {
                        break;
                    }
                }
                resolveInfoList.addAll(homeInfoList);

                if (newInstall) { // New app, simple adding
                    for (ResolveInfo info : resolveInfoList) {
                        appInfoList.add(new AppInfo(mContext, info));
                    }
                } else { // Update, merge data
                    for (ResolveInfo info : resolveInfoList) {
                        AppInfo newAppInfo = new AppInfo(mContext, info);

                        Iterator<AppInfo> oldInfoIterator = matchedAppInfoList.iterator();
                        while (oldInfoIterator.hasNext()) {
                            AppInfo oldInfo = oldInfoIterator.next();
                            if (oldInfo.getActivityName().equals(newAppInfo.getActivityName())) {
                                if (oldInfo.getLabelMode() == 2) { // AppInfo is alias
                                    oldInfo.setLabel(newAppInfo.getLabel());
                                    oldInfo.setInstallTime(newAppInfo.getInstallTime());
                                    appInfoList.add(oldInfo);
                                } else {
                                    newAppInfo.setCallCount(oldInfo.getCallCount());
                                    newAppInfo.setPinMode(oldInfo.getPinMode());
                                    newAppInfo.setLabelMode(oldInfo.getLabelMode());
                                    newAppInfo.setOverrideLabel(oldInfo.getOverrideLabel());
                                }
                                oldInfoIterator.remove();
                            }
                        }
                        appInfoList.add(newAppInfo);
                    }
                }
            }

            save(appInfoList);
            mContext = null;
            long end = System.currentTimeMillis();
            long duration = end - start;
            Log.d(App.LOG_TAG, "BroadcastReceiver ran " + duration + "ms.");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (Build.VERSION.SDK_INT >= 11) {
                // Must call finish() so the BroadcastReceiver can be recycled.
                mPendingResult.finish();
            }
        }
    }
}