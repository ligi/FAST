package org.ligi.fast.model;

import java.util.Comparator;
import java.util.Locale;

public class AppInfoSortByMostUsedComparator implements Comparator<AppInfo> {

    //private final Comparator<AppInfo> sortByLabel = new AppInfoSortByLabelComparator();
    private final Comparator<AppInfo> sortByPin = new AppInfoSortByPinComparator();

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = sortByPin.compare(lhs, rhs);

        if (result == 0) {
            if (lhs.getCallCount() == rhs.getCallCount()) {
                //result = sortByLabel.compare(lhs, rhs);
                final String lhsLowerCaseLabel = lhs.getLabel().toLowerCase(Locale.ENGLISH);
                final String rhsLowerCaseLabel = rhs.getLabel().toLowerCase(Locale.ENGLISH);
                result = lhsLowerCaseLabel.compareTo(rhsLowerCaseLabel);
            } else if (lhs.getCallCount() < rhs.getCallCount()) {
                result = 1;
            } else if (lhs.getCallCount() > rhs.getCallCount()) {
                result = -1;
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
