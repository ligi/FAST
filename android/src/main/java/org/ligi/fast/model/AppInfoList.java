package org.ligi.fast.model;

import org.ligi.fast.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppInfoList extends ArrayList<AppInfo> implements App.PackageChangedListener {

    public void update(List<AppInfo> newList) {
        // check if building and applying a diff is better and not
        clear();
        addAll(newList);
    }

    @Override
    public AppInfo get(int position) {
        // TODO should do this one at the root of the problem

        if (size() > 0 && (position+1  > size())) {
            return super.get(0);
        }
        return super.get(position);
    }

    @Override
    public void onPackageChange(List<String> changedPackageNames, List<AppInfo> newAppInfoList) {
        List<AppInfo> updatedAppInfoList = new ArrayList<>();
        List<AppInfo> matchedAppInfoList = new ArrayList<>();
        if (changedPackageNames == null) {
            matchedAppInfoList.addAll(this);
        } else {
            // Collect existing records of changed packages and carry over unchanged ones
            next_record:
            for (AppInfo existingAppInfo : this) {
                for (String packageName : changedPackageNames) {
                    if (existingAppInfo.getPackageName().equals(packageName)) {
                        matchedAppInfoList.add(existingAppInfo);
                        continue next_record;
                    }
                }
                updatedAppInfoList.add(existingAppInfo);
            }
        }

        if (newAppInfoList == null) {
        } else if (matchedAppInfoList.size() == 0) { // New app. Just add new records.
            updatedAppInfoList.addAll(newAppInfoList);
        } else {
            // Update and carry over all existing records that still apply and add new records
            for (AppInfo newAppInfo : newAppInfoList) {
                boolean isKnownRecord = false;
                for (Iterator<AppInfo> iterator = matchedAppInfoList.iterator(); iterator.hasNext(); ) {
                    AppInfo existingAppInfoItem = iterator.next();
                    if (existingAppInfoItem.isSameActivity(newAppInfo)) {
                        existingAppInfoItem.updateInfo(newAppInfo);
                        updatedAppInfoList.add(existingAppInfoItem);
                        isKnownRecord = true;
                        iterator.remove();
                    }
                }
                if (!isKnownRecord) {
                    updatedAppInfoList.add(newAppInfo);
                }
            }
        }

        this.clear();
        this.addAll(updatedAppInfoList);
    }
}
