package org.ligi.fast.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.fast.App;
import org.ligi.fast.R;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to provide the AppInfo to the user - also care for the filtering of the query
 */
public class AppInfoAdapter extends BaseAdapter {

    private final Context ctx;

    private String highLightColorHexString = "";
    private final AppInfoList appInfoList;


    public AppInfoAdapter(Context ctx, List<AppInfo> pkgAppsListAll) {
        this.ctx = ctx;
        appInfoList = new AppInfoList(pkgAppsListAll, App.getSettings());

        int color = (ctx.getResources().getColor(R.color.divider_color));
        highLightColorHexString = Integer.toHexString(color).toUpperCase().substring(2);
    }


    public int getCount() {
        return appInfoList.getCount();
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
            if (App.getSettings().isTextOnlyActivated()) {
                convertView = mLayoutInflater.inflate(R.layout.item_textonly, null);
            } else {
                String size = (App.getSettings().getIconSize());
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

                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.getLayoutParams().height = iconSize;
                imageView.getLayoutParams().width = iconSize;
            }
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.textView);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        ImageView imageView = holder.image;
        TextView labelView = holder.text;

        AppInfo actAppInfo = appInfoList.get(position);


        if (imageView != null) {
            Drawable drawable = actAppInfo.getIcon();
            holder.image.setImageDrawable(drawable);
        }

        labelView.setMaxLines(App.getSettings().getMaxLines());

        String label = actAppInfo.getLabel();
        String highlight_label = label;

        int query_index = label.toLowerCase().indexOf(appInfoList.getCurrentQuery());

        if (appInfoList.getCurrentQuery().length() == 0) {
            labelView.setText(label);
            return convertView;
        }

        if (query_index == -1) { // search not App-Name - hope it is in Package Name - why else we want to show the app?
            label = actAppInfo.getPackageName();
            label = label.replace("com.google.android.apps.", "");
            query_index = label.toLowerCase().indexOf(appInfoList.getCurrentQuery());
        }

        if (query_index != -1) {
            highlight_label = label.substring(0, query_index)
                    + "<font color='#"
                    + highLightColorHexString
                    + "'>"
                    + label.substring(query_index,
                    query_index + appInfoList.getCurrentQuery().length())
                    + "</font>"
                    + label.substring(query_index + appInfoList.getCurrentQuery().length(),
                    label.length());
        } else {
            // highlight single characters of query in label for fuzzy matched strings
            label = actAppInfo.getLabel();
            ArrayList<Integer> matchedIndices = StringUtils.getMatchedIndices(label,
                    appInfoList.getCurrentQuery());
            ArrayList<String> tokens = StringUtils.splitWithIndices(label, matchedIndices);

            if (matchedIndices.size() > 0) {
                highlight_label = "";
                for (int i = 0; i < tokens.size(); i = i + 2) {
                    if (i + 1 < tokens.size()) {
                        highlight_label += tokens.get(i);
                        highlight_label += "<font color='#"
                                + highLightColorHexString
                                + "'>"
                                + tokens.get(i + 1)
                                + "</font>";
                    } else {
                        highlight_label += tokens.get(i);
                    }
                }
            }
        }

        labelView.setText(Html.fromHtml(highlight_label));
        return convertView;
    }


    public void setActQuery(String act_query) {
        appInfoList.setQuery(act_query);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public AppInfoList getList() {
        return appInfoList;
    }

}