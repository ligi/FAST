package org.ligi.fast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to provide the AppInfo to the user - also care for the filtering of the query
 */
public class AppInfoAdapter extends BaseAdapter {

    private static List<AppInfo> pkgAppsListShowing;
    private static List<AppInfo> pkgAppsListAll;
    private Context ctx;
    private String actQuery = "";
    private String colorString = "";
    private SortMode sortMode = SortMode.UNSORTED;

    public enum SortMode {
        UNSORTED, ALPHABETICAL
    }

    public AppInfoAdapter(Context _ctx, List<AppInfo> _pkgAppsListAll) {
        ctx = _ctx;
        setAllAppsList(_pkgAppsListAll);
    }

    @SuppressWarnings("unchecked")
    public void setAllAppsList(List<AppInfo> _pkgAppsListAll) {
        pkgAppsListAll = new ArrayList<AppInfo>();
        pkgAppsListAll.addAll(_pkgAppsListAll);

        new IconCacheTask().execute(pkgAppsListAll);

        setActQuery(actQuery); // to rebuild the showing list

        int color = (ctx.getResources().getColor(R.color.divider_color));
        colorString = Integer.toHexString(color).toUpperCase().substring(2);
    }

    public void setSortMode(SortMode mode) {
        sortMode = mode;
        if (sortMode.equals(SortMode.ALPHABETICAL))
            java.util.Collections.sort(pkgAppsListAll, new AppInfoSortByLabelComparator());
    }

    public int getCount() {
        return pkgAppsListShowing.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) { // || ((ViewHolder)convertView.getTag()).isTextOnlyActive == getSettings().isTextOnlyActive()) { // if it's not recycled, initialize some

            LayoutInflater mLayoutInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (App.getSettings().isTextOnlyActive())
                convertView = mLayoutInflater.inflate(R.layout.item_textonly, null);
            else {
                String size = (new FASTSettings(ctx).getIconSize());
                int cellSize;
                int iconSize;

                if (size.equals("tiny")) {
                    cellSize = R.dimen.cell_size_tiny;
                    iconSize = R.dimen.icon_size_tiny;
                } else if (size.equals("small")) {
                    cellSize = R.dimen.cell_size_small;
                    iconSize = R.dimen.icon_size_small;
                } else if (size.equals("large")) {
                    cellSize = R.dimen.cell_size_large;
                    iconSize = R.dimen.icon_size_large;
                } else {
                    cellSize = R.dimen.cell_size;
                    iconSize = R.dimen.icon_size;
                }

                cellSize = parent.getContext().getResources().getDimensionPixelSize(cellSize);
                iconSize = parent.getContext().getResources().getDimensionPixelSize(iconSize);

                convertView = mLayoutInflater.inflate(R.layout.item_icon, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(cellSize, ViewGroup.LayoutParams.WRAP_CONTENT));

                ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
                imageView.getLayoutParams().height = iconSize;
                imageView.getLayoutParams().width  = iconSize;
            }
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.textView);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        ImageView imageView = holder.image;
        TextView labelView = holder.text;
        if (imageView != null) {
            Drawable drawable = pkgAppsListShowing.get(position).getIcon();
            holder.image.setImageDrawable(drawable);
        }

        labelView.setMaxLines(App.getSettings().getMaxLines());

        String label = pkgAppsListShowing.get(position).getLabel();
        String hightlight_label = label;

        int query_index = label.toLowerCase().indexOf(actQuery);

        if (actQuery.length() == 0) {
            labelView.setText(label);
            return convertView;
        }

        if (query_index == -1) { // search not App-Name - hope it is in Package Name - why else we want to show the app?
            label = pkgAppsListShowing.get(position).getPackageName();
            label = label.replace("com.google.android.apps.", "");
            query_index = label.toLowerCase().indexOf(actQuery);
        }

        if (query_index != -1) {
            hightlight_label = label.substring(0, query_index)
                    + "<font color='#"
                    + colorString
                    + "'>"
                    + label.substring(query_index,
                    query_index + actQuery.length())
                    + "</font>"
                    + label.substring(query_index + actQuery.length(),
                    label.length());
        }

        labelView.setText(Html.fromHtml(hightlight_label));
        return convertView;
    }


    private boolean appInfoMatchesQuery(AppInfo info, String query) {
        if (info.getLabel().toLowerCase().contains(actQuery)) {
            return true;
        }

        // also search in package name when activated
        if (App.getSettings().isSearchPackageActivated() && (info.getPackageName().toLowerCase().contains(actQuery))) {
            return true;
        }

        return false; // no match till here - we must be false
    }

    public void setActQuery(String act_query) {
        // note the alternate query approach is not exact - doesn't match all permutations of replacements, but
        // is FASTer than exact and totally enough for most cases
        String actAlternateQuery;

        if (App.getSettings().isUmlautConvertActivated()) {
            actAlternateQuery = act_query.replaceAll("ue", "ü").replaceAll("oe", "ö").replaceAll("ae", "ä").replaceAll("ss", "ß");
        } else {
            actAlternateQuery = null;
        }

        this.actQuery = act_query;

        ArrayList<AppInfo> pkgAppsListFilter = new ArrayList<AppInfo>();

        for (AppInfo info : pkgAppsListAll) {
            if (appInfoMatchesQuery(info, act_query) || appInfoMatchesQuery(info, actAlternateQuery)) {
                pkgAppsListFilter.add(info);
            }
        }

        pkgAppsListShowing = pkgAppsListFilter;
        notifyDataSetChanged();
    }

    public AppInfo getAtPosition(int pos) {
        return pkgAppsListShowing.get(pos);
    }

    private static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    private static class IconCacheTask extends AsyncTask<List<AppInfo>, Void, Void> {
        protected Void doInBackground(List<AppInfo>... params) {
            List<AppInfo> all = params[0];
            for (AppInfo info:all) {
                info.getIcon();
            }
            return null;
        }
    }

}