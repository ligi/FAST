package org.ligi.fast.model;

import java.util.Comparator;

public class AppInfoSortByLastInstalled implements Comparator<AppInfo> {

    private final Comparator<AppInfo> sortByLabel = new AppInfoSortByLabelComparator();
    private final Comparator<AppInfo> sortByPin = new AppInfoSortByPinComparator();

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = sortByPin.compare(lhs, rhs);

        if (result != 0) {
            return result;
        }
        
        if (lhs.getInstallTime() == rhs.getInstallTime()) {
            result = sortByLabel.compare(lhs, rhs);
        } else if (lhs.getInstallTime() < rhs.getInstallTime()) {
            result = 1;
        } else if (lhs.getInstallTime() > rhs.getInstallTime()) {
            result = -1;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
