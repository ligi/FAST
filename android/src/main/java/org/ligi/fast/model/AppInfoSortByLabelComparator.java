package org.ligi.fast.model;

import java.util.Comparator;
import java.util.Locale;

class AppInfoSortByLabelComparator implements Comparator<AppInfo> {

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        final String lhsLowerCaseLabel = lhs.getLabel().toLowerCase(Locale.ENGLISH);
        final String rhsLowerCaseLabel = rhs.getLabel().toLowerCase(Locale.ENGLISH);
        return lhsLowerCaseLabel.compareTo(rhsLowerCaseLabel);
    }

}
