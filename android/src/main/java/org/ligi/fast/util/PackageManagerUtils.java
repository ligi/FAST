package org.ligi.fast.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PackageManagerUtils {
    /**
     * Get a ResolveInfoList with all activities that are relevant to FAST.
     * In addition to all activities with ACTION_MAIN and CATEGORY_LAUNCHER, FAST also considers
     * any other Launcher on the device. This list will never contain FAST's own activities.
     * <p>
     * Optionally, the query can be limited to a custom set of packages.
     *
     * @param context      A reference to the application context
     * @param packageNames The package names to query. If this is null, all packages are considered.
     * @return Relevant activities from the given packages or all packages if packageNames is null.
     * The return value will never be null.
     */
    @NonNull
    public static List<ResolveInfo> getResolveInfo(Context context, @Nullable List<String> packageNames) {
        List<String> ignoredPackages = new ArrayList<>(Arrays.asList(
                context.getPackageName()
        ));
        List<String> ignoredLaunchers = new ArrayList<>(Arrays.asList(
                "com.android.settings"
        ));
        ignoredLaunchers.addAll(ignoredPackages);
        List<ResolveInfo> activitiesResolveInfoList = new ArrayList<>();
        Intent activitiesIntent = new Intent(Intent.ACTION_MAIN);
        Intent launchersIntent = new Intent(Intent.ACTION_MAIN);
        activitiesIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchersIntent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = context.getPackageManager();

        if (packageNames == null) {
            activitiesResolveInfoList.addAll(mergeWithoutDuplicates(
                    ignorePackages(pm.queryIntentActivities(activitiesIntent, 0), ignoredPackages),
                    ignorePackages(pm.queryIntentActivities(launchersIntent, 0), ignoredLaunchers)));
        } else {
            next_package:
            for (String packageName : packageNames) {
                for (String ignore : ignoredPackages) {
                    if (packageName.equals(ignore)) {
                        continue next_package;
                    }
                }
                activitiesIntent.setPackage(packageName);
                for (String ignore : ignoredLaunchers) {
                    if (packageName.equals(ignore)) {
                        activitiesResolveInfoList.addAll(pm.queryIntentActivities(activitiesIntent, 0));
                        continue next_package;
                    }
                }
                launchersIntent.setPackage(packageName);
                activitiesResolveInfoList.addAll(mergeWithoutDuplicates(
                        pm.queryIntentActivities(activitiesIntent, 0),
                        pm.queryIntentActivities(launchersIntent, 0)));
            }
        }

        return activitiesResolveInfoList;
    }

    /**
     * Remove packages from a ResolveInfo list.
     *
     * @param resolveInfoList ResolveInfo items to filter
     * @param ignoredPackages List of packages to remove
     * @return The same resolveInfoList for method chaining
     */
    private static List<ResolveInfo> ignorePackages(List<ResolveInfo> resolveInfoList, List<String> ignoredPackages) {
        Iterator<ResolveInfo> resolveInfoIterator = resolveInfoList.iterator();
        while (resolveInfoIterator.hasNext()) {
            ResolveInfo resolveInfo = resolveInfoIterator.next();
            for (String ignore : ignoredPackages) {
                if (resolveInfo.activityInfo.packageName.equals(ignore)) {
                    resolveInfoIterator.remove();
                    break;
                }
            }
        }
        return resolveInfoList;
    }

    /**
     * Merge a secondary ResolveInfoList into another one if it doesn't already contain an entry
     * with the same activity name. This is useful because an activity can have both the LAUNCHER
     * and the HOME category and thus show up in both listings. FAST's own SearchActivity is one
     * example for this.
     *
     * @param main      Include all activities from this list
     * @param secondary Include all unique activities from this list
     * @return A new list that contains all merged activities
     */
    private static List<ResolveInfo> mergeWithoutDuplicates(List<ResolveInfo> main, List<ResolveInfo> secondary) {
        List<ResolveInfo> merged = new ArrayList<>(main);
        outer_loop:
        for (ResolveInfo secondInfo : secondary) {
            for (ResolveInfo mainInfo : main) {
                if (mainInfo.activityInfo.packageName.equals(secondInfo.activityInfo.packageName)
                        && mainInfo.activityInfo.name.equals(secondInfo.activityInfo.name)) {
                    continue outer_loop;
                }
            }
            merged.add(secondInfo);
        }
        return merged;
    }
}
