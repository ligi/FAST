package org.ligi.fast.model;

import java.util.Comparator;

public class AppInfoSortByLastUsedComparator implements Comparator<AppInfo> {

    private final Comparator<AppInfo> sortByLabel = new AppInfoSortByLabelComparator();

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = 0;

        if (lhs.getLastUsedTime() == rhs.getLastUsedTime()) {
            result = sortByLabel.compare(lhs, rhs);
        } else if (lhs.getLastUsedTime() < rhs.getLastUsedTime()) {
            result = 1;
        } else if (lhs.getLastUsedTime() > rhs.getLastUsedTime()) {
            result = -1;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
