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
import org.ligi.fast.model.DynamicAppInfoList;
import org.ligi.fast.util.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter to provide the AppInfo to the user - also care for the filtering of the query
 */
public class AppInfoAdapter extends BaseAdapter {

    private final String highlightPrefix;
    private final static String highlightSuffix = "</font>";

    private final DynamicAppInfoList appInfoList;
    private final LayoutInflater layoutInflater;
    private final IconDimensions iconDimensions;

    public AppInfoAdapter(Context ctx, DynamicAppInfoList appInfoList) {
        this.appInfoList = appInfoList;

        int color = (ctx.getResources().getColor(R.color.divider_color));
        final String hexColorString = Integer.toHexString(color).toUpperCase(Locale.ENGLISH).substring(2);
        highlightPrefix = "<font color='#" + hexColorString +"'>";
        layoutInflater = LayoutInflater.from(ctx);
        iconDimensions = new IconDimensions(ctx);
    }


    public int getCount() {
        return appInfoList.size();
    }

    public AppInfo getItem(int position) {
        return appInfoList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) { // || ((ViewHolder)convertView.getTag()).isTextOnlyActive == getSettings().isTextOnlyActive()) { // if it's not recycled, initialize some
            if (App.getSettings().isTextOnlyActivated()) {
                convertView = layoutInflater.inflate(R.layout.item_textonly, null);
            } else {

                convertView = layoutInflater.inflate(R.layout.item_icon, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(iconDimensions.outerSizeInPx, ViewGroup.LayoutParams.WRAP_CONTENT));

                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.getLayoutParams().height = iconDimensions.innerSizeInPx;
                imageView.getLayoutParams().width = iconDimensions.innerSizeInPx;
            }
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.textView);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        ImageView imageView = holder.image;
        TextView labelView= holder.text;

        AppInfo actAppInfo = appInfoList.get(position);

        if (imageView != null) {
            Drawable drawable = actAppInfo.getIcon();
            holder.image.setImageDrawable(drawable);
        }

        final int maxLines = App.getSettings().getMaxLines();

        if (maxLines==0) {
            labelView.setVisibility(View.GONE);
        } else {
            labelView.setLines(maxLines);
            labelView.setVisibility(View.VISIBLE);
        }

        String label = actAppInfo.getDisplayLabel();
        String highlight_label = label;

        int query_index = label.toLowerCase(Locale.ENGLISH).indexOf(appInfoList.getCurrentQuery());

        if (appInfoList.getCurrentQuery().length() == 0) {
            labelView.setText(label);
            
            return convertView;
        }

        if (query_index == -1) { // search not App-Name - hope it is in Package Name - why else we want to show the app?
            label = actAppInfo.getPackageName();
            label = label.replace("com.google.android.apps.", "");
            query_index = label.toLowerCase(Locale.ENGLISH).indexOf(appInfoList.getCurrentQuery());
        }


        if (query_index != -1) {
            highlight_label = label.substring(0, query_index)
                    + highlightPrefix
                    + label.substring(query_index,query_index + appInfoList.getCurrentQuery().length())
                    + highlightSuffix
                    + label.substring(query_index + appInfoList.getCurrentQuery().length(),
                    label.length());
        } else if (App.getSettings().isGapSearchActivated()) {

            final ArrayList<Integer> matchedIndices = StringUtils.getMatchedIndices(actAppInfo.getDisplayLabel(),appInfoList.getCurrentQuery());
            if (matchedIndices.size()==appInfoList.getCurrentQuery().length()) {
                // highlight single characters of query in label for gap matched strings
                label = actAppInfo.getDisplayLabel();
            } // otherwise must be in package

            highlight_label="";
            int i=0;
            for (char chr:label.toCharArray()) {
                if (matchedIndices.contains(i++)) {
                    highlight_label+=highlightPrefix+chr+highlightSuffix;
                } else {
                    highlight_label+=chr;
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

    public void setSortMode(final DynamicAppInfoList.SortMode sortMode) {
        appInfoList.setSortMode(sortMode);
        notifyDataSetChanged();
    }
}
