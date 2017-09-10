package org.ligi.fast.model;

import java.util.Comparator;

public class AppInfoSortByLastInstalled implements Comparator<AppInfo> {

    private final Comparator<AppInfo> sortByLabel = new AppInfoSortByLabelComparator();
    private final Comparator<AppInfo> sortByPin = new AppInfoSortByPinComparator();
    private int a = -1, b = 1;

    AppInfoSortByLastInstalled() {}

    AppInfoSortByLastInstalled(boolean descending) {
        if (descending) {
            a = 1;
            b = -1;
        }
    }

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = sortByPin.compare(lhs, rhs);;

        
        if (lhs.getInstallTime() == rhs.getInstallTime()) {
            result = sortByLabel.compare(lhs, rhs);
        } else if (lhs.getInstallTime() < rhs.getInstallTime()) {
            result = a;
        } else if (lhs.getInstallTime() > rhs.getInstallTime()) {
            result = b;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
