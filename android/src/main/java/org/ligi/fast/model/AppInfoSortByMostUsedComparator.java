package org.ligi.fast.model;

import java.util.Comparator;

public class AppInfoSortByMostUsedComparator implements Comparator<AppInfo> {

    private final Comparator<AppInfo> sortByLabel = new AppInfoSortByLabelComparator();

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = 0;

        if (lhs.getCallCount() == rhs.getCallCount()) {
            result = sortByLabel.compare(lhs, rhs);
        } else if (lhs.getCallCount() < rhs.getCallCount()) {
            result = 1;
        } else if (lhs.getCallCount() > rhs.getCallCount()) {
            result = -1;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
