package org.ligi.fast.model;

import android.os.AsyncTask;

import org.ligi.fast.settings.FASTSettings;
import org.ligi.fast.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AppInfoList {

    private List<AppInfo> pkgAppsListShowing;
    private List<AppInfo> pkgAppsListAll;
    private String currentQuery = "";
    private final FASTSettings settings;

    public enum SortMode {
        UNSORTED, ALPHABETICAL
    }

    @SuppressWarnings("unchecked")
    public AppInfoList(List<AppInfo> pkgAppsListAll, FASTSettings settings) {
        this.settings = settings;
        setAppsList(pkgAppsListAll);
    }

    public void setAppsList(List<AppInfo> pkgAppsListAll) {
        this.pkgAppsListAll = new ArrayList<AppInfo>();
        this.pkgAppsListAll.addAll(pkgAppsListAll);

        new IconCacheTask().execute(this.pkgAppsListAll);

        setQuery(currentQuery); // to rebuild the showing list
    }

    public void setSortMode(SortMode mode) {
        if (mode.equals(SortMode.ALPHABETICAL)) {
            java.util.Collections.sort(pkgAppsListAll, new AppInfoSortByLabelComparator());
        }

        setQuery(currentQuery); // refresh showing
    }

    public int getCount() {
        return pkgAppsListShowing.size();
    }

    public AppInfo get(int pos) {
        if (pos >= getCount()) {
            return get(0); // the first one for the rescue
        }
        return pkgAppsListShowing.get(pos);
    }

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
        currentQuery = currentQuery.toLowerCase();

        ArrayList<AppInfo> pkgAppsListFilter = new ArrayList<AppInfo>();

        for (AppInfo info : pkgAppsListAll) {
            if (appInfoMatchesQuery(info, currentQuery)) {
                pkgAppsListFilter.add(info);
            }
        }

        pkgAppsListShowing = pkgAppsListFilter;
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
        if (info.getLabel().toLowerCase().contains(query)) {
            return true;
        }

        if (settings.isUmlautConvertActivated() && info.getAlternateLabel() != null && info.getAlternateLabel().toLowerCase().contains(query)) {
            return true;
        }


        if (isGapSearchActivateAndTrueForQuery(info, query)) {
            return true;
        }

        // also search in package name when activated
        // TBD should we also do gap search in package name?
        if (settings.isSearchPackageActivated()) {
            if (settings.isUmlautConvertActivated() && info.getAlternatePackageName() != null && info.getAlternatePackageName().toLowerCase().contains(query)) {
                return true;
            }

            return (info.getPackageName().toLowerCase().contains(query));
        }

        // no match
        return false;
    }

    private boolean isGapSearchActivateAndTrueForQuery(AppInfo info, String query) {
        if (settings.isGapSearchActivated()) {
            String appLabelLowerCase = info.getLabel().toLowerCase();
            int diffLength = appLabelLowerCase.length() - query.length();
            int threshold = diffLength > 0 ? diffLength : 0;

            if (StringUtils.getLevenshteinDistance(appLabelLowerCase, query, threshold) != -1) {
                return true;
            }
        }
        return false;
    }

}