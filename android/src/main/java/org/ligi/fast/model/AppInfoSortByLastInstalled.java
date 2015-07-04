package org.ligi.fast.model;

import java.util.Comparator;

public class AppInfoSortByLastInstalled implements Comparator<AppInfo> {

    private final Comparator<AppInfo> sortByLabel = new AppInfoSortByLabelComparator();

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = 0;

        
        if (lhs.getInstallTime() == rhs.getInstallTime()) {
            result = sortByLabel.compare(lhs, rhs);
        } else if (lhs.getInstallTime() < rhs.getInstallTime()) {
            result = -1;
        } else if (lhs.getInstallTime() > rhs.getInstallTime()) {
            result = 1;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
