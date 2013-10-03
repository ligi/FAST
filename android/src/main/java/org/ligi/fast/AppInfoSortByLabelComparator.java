package org.ligi.fast;

import java.util.Comparator;

class AppInfoSortByLabelComparator implements Comparator<AppInfo> {

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        return lhs.getLabel().compareTo(rhs.getLabel());
    }

}
