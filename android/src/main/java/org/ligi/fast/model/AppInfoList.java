package org.ligi.fast.model;

import java.util.ArrayList;
import java.util.List;

public class AppInfoList extends ArrayList<AppInfo> {

    public void update(List<AppInfo> newList) {
        // check if building and applying a diff is better and not
        clear();
        addAll(newList);
    }

    @Override
    public AppInfo get(int position) {
        // TODO should do this one
        if (size() > 0 && (position - 1) > size()) {
            return super.get(0);
        }
        return super.get(position);
    }
}