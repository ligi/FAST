package org.ligi.fast.model;

import java.util.Comparator;

class AppInfoSortOldestFirst implements Comparator<AppInfo> {
    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = 0;

        if (lhs.getInstallTime() == rhs.getInstallTime()) {
            result = new AppInfoSortByLabelComparator().compare(lhs, rhs);
        } else if (lhs.getInstallTime() < rhs.getInstallTime()) {
            result = -1;
        } else {
            result = 1;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
