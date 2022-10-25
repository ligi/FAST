package org.ligi.fast.model;

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
        UNSORTED, ALPHABETICAL, MOST_USED, LAST_INSTALLED
    }

    public DynamicAppInfoList(List<AppInfo> backingAppInfoList, FASTSettings settings) {
        this.settings = settings;
        this.backingAppInfoList=new ArrayList<>();
        update(backingAppInfoList);
    }

    @Override
    public void update(List<AppInfo> pkgAppsListAll) {
        final List<AppInfo> appsToRemove = new ArrayList<>();
        for (AppInfo localApp : backingAppInfoList) {
            if (getAppWithHash(localApp.getHash(), pkgAppsListAll) == null) {
                appsToRemove.add(localApp);
            }
        }
        backingAppInfoList.removeAll(appsToRemove);

        for (AppInfo app : pkgAppsListAll) {
            final AppInfo appWithHash = getAppWithHash(app.getHash(), backingAppInfoList);
            if (appWithHash != null) {
                if (app.getLabelMode() == 2) {
                    final AppInfoList aliasesWithHash = getAliasesWithHash(app.getHash(), backingAppInfoList);
                    if (!aliasesWithHash.contains(app)) {
                        backingAppInfoList.add(app);
                    }
                } else {
                    appWithHash.mergeSafe(app);
                }
            } else {
                backingAppInfoList.add(app);
            }
        }

        setSortMode(currentSortMode);
    }

    public void setSortMode(SortMode mode) {
        currentSortMode = mode;
        sorter=null;
        if (mode.equals(SortMode.ALPHABETICAL)) {
            sorter = new AppInfoSortByLabelComparator();
        } else if (mode.equals(SortMode.MOST_USED)) {
            sorter = new AppInfoSortByMostUsedComparator();
        } else if (mode.equals(SortMode.LAST_INSTALLED)) {
            sorter = new AppInfoSortByLastInstalled();
        } else {
            sorter = new AppInfoSortByPinComparator();
        }
        setQuery(currentQuery); // refresh showing
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
        
        if (sorter != null) {
            java.util.Collections.sort(filteredAppInfoList, sorter);
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
        if (!settings.isShowHiddenActivated() && info.getPinMode() == -1) {
            return false;
        }

        if (info.getDisplayLabel().toLowerCase(Locale.ENGLISH).contains(query)) {
            return true;
        }

        if (settings.isUmlautConvertActivated()
                && info.getAlternateDisplayLabel() != null
                && info.getAlternateDisplayLabel().toLowerCase(Locale.ENGLISH).contains(query)) {
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
            final String appLabelLowerCase = info.getDisplayLabel().toLowerCase(Locale.ENGLISH);
            final int diffLength = appLabelLowerCase.length() - query.length();
            final int threshold = diffLength > 0 ? diffLength : 0;

            if (StringUtils.getLevenshteinDistance(appLabelLowerCase, query, threshold) != -1) {
                return true;
            }
        }
        return false;
    }

    public List<AppInfo> getBackingAppInfoList() {
        return backingAppInfoList;
    }
}
