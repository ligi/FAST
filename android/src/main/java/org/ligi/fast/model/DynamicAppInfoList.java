package org.ligi.fast.model;

import android.os.AsyncTask;

import org.ligi.fast.settings.FASTSettings;
import org.ligi.fast.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * a dynamic AppInfoList is a AppInfoList that is sortable and queryable
 */
public class DynamicAppInfoList extends AppInfoList {

    private List<AppInfo> backingAppInfoList;

    private String currentQuery = "";
    private final FASTSettings settings;
    private Comparator<AppInfo> sorter = null;
    private SortMode currentSortMode = SortMode.UNSORTED;

    public enum SortMode {
        UNSORTED, ALPHABETICAL, MOST_USED
    }

    @SuppressWarnings("unchecked")
    public DynamicAppInfoList(List<AppInfo> backingAppInfoList, FASTSettings settings) {
        this.settings = settings;
        update(backingAppInfoList);
    }

    @Override
    public void update(List<AppInfo> pkgAppsListAll) {
        super.update(pkgAppsListAll);

        this.backingAppInfoList = new ArrayList<AppInfo>();
        this.backingAppInfoList.addAll(pkgAppsListAll);

        new IconCacheTask().execute(this.backingAppInfoList);

        setSortMode(currentSortMode);
    }

    public void setSortMode(SortMode mode) {
        currentSortMode = mode;
        if (mode.equals(SortMode.ALPHABETICAL)) {
            this.sorter = new AppInfoSortByLabelComparator();
        } else if (mode.equals(SortMode.MOST_USED)) {
            this.sorter = new AppInfoSortByMostUsedComparator();
        }
        sort();
        setQuery(currentQuery); // refresh showing
    }

    private void sort() {
        if (this.sorter != null) {
            java.util.Collections.sort(backingAppInfoList, this.sorter);
        }
    }

/*
    public AppInfo get(int pos) {
        if (pos >= size()) {
            return get(0); // the first one for the rescue
        }
        return get(pos);
    }
*/
    private static class IconCacheTask extends AsyncTask<List<AppInfo>, Void, Void> {
        protected Void doInBackground(List<AppInfo>... params) {
            List<AppInfo> all = params[0];
            for (AppInfo info : all) {
                info.getIcon();
            }
            return null;
        }
    }

    public void setQuery(String act_query) {

        currentQuery = configuredRemoveTrailingSpace(act_query);
        currentQuery = currentQuery.toLowerCase(Locale.ENGLISH);

        final AppInfoList filteredAppInfoList = new AppInfoList();

        for (AppInfo info : backingAppInfoList) {
            if (appInfoMatchesQuery(info, currentQuery)) {
                filteredAppInfoList.add(info);
            }
        }

        super.update(filteredAppInfoList);

    }

    private String configuredRemoveTrailingSpace(String act_query) {
        if (settings.isIgnoreSpaceAfterQueryActivated()) {
            if (act_query.endsWith(" ")) {
                act_query = act_query.substring(0, act_query.length() - 1);
            }
        }
        return act_query;
    }

    public String getCurrentQuery() {
        return currentQuery;
    }

    private boolean appInfoMatchesQuery(AppInfo info, String query) {
        if (info.getLabel().toLowerCase(Locale.ENGLISH).contains(query)) {
            return true;
        }

        if (settings.isUmlautConvertActivated()
                && info.getAlternateLabel() != null
                && info.getAlternateLabel().toLowerCase(Locale.ENGLISH).contains(query)) {
            return true;
        }


        if (isGapSearchActivateAndTrueForQuery(info, query)) {
            return true;
        }

        // also search in package name when activated
        // TBD should we also do gap search in package name?
        if (settings.isSearchPackageActivated()) {
            if (settings.isUmlautConvertActivated()
                    && info.getAlternatePackageName() != null
                    && info.getAlternatePackageName().toLowerCase(Locale.ENGLISH).contains(query)) {
                return true;
            }

            return info.getPackageName().toLowerCase(Locale.ENGLISH).contains(query);
        }

        // no match
        return false;
    }

    private boolean isGapSearchActivateAndTrueForQuery(AppInfo info, String query) {
        if (settings.isGapSearchActivated()) {
            final String appLabelLowerCase = info.getLabel().toLowerCase(Locale.ENGLISH);
            int diffLength = appLabelLowerCase.length() - query.length();
            int threshold = diffLength > 0 ? diffLength : 0;

            if (StringUtils.getLevenshteinDistance(appLabelLowerCase, query, threshold) != -1) {
                return true;
            }
        }
        return false;
    }

    public List<AppInfo> getAll() {
        return backingAppInfoList;
    }

}