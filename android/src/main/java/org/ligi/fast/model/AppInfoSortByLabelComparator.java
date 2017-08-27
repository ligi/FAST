package org.ligi.fast.model;

import java.util.Comparator;
import java.util.Locale;

class AppInfoSortByLabelComparator implements Comparator<AppInfo> {

    private final Comparator<AppInfo> sortByPin = new AppInfoSortByPinComparator();

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        int result = sortByPin.compare(lhs, rhs);
        if (result == 0) {
            final String lhsLowerCaseLabel = lhs.getDisplayLabel().toLowerCase(Locale.ENGLISH);
            final String rhsLowerCaseLabel = rhs.getDisplayLabel().toLowerCase(Locale.ENGLISH);
            return lhsLowerCaseLabel.compareTo(rhsLowerCaseLabel);
        }
        else {
            return result;
        }
    }

}
