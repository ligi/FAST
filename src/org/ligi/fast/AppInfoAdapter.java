package org.ligi.fast;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to provide the AppInfo to the user - also care for the filtering of the query
 *
 * @author Marcus -ligi- Büschleb
 *         <p/>
 *         License GPLv3
 */
public class AppInfoAdapter extends BaseAdapter {

    private Context ctx;
    private List<AppInfo> pkgAppsListAll;
    private List<AppInfo> pkgAppsListShowing;
    private String act_query = "";

    public AppInfoAdapter(Context _ctx, List<AppInfo> _pkgAppsListAll) {
        ctx = _ctx;
        setAllAppsList(_pkgAppsListAll);
    }

    public void setAllAppsList(List<AppInfo> _pkgAppsListAll) {
        pkgAppsListAll = new ArrayList<AppInfo>();
        pkgAppsListAll.addAll(_pkgAppsListAll);
        setActQuery(act_query); // to rebuild the showing list
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

        if (convertView == null || ((ViewHolder)convertView.getTag()).isTextOnlyActive == getPrefs().isTextOnlyActive()) { // if it's not recycled, initialize some

            LayoutInflater mLayoutInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (getPrefs().isTextOnlyActive())
                convertView = mLayoutInflater.inflate(R.layout.item_textonly, null);
            else {
                String size = (new FASTPrefs(ctx).getIconSize());

                if (size.equals("small"))
                    convertView = mLayoutInflater.inflate(R.layout.item_small, null);
                else if (size.equals("large"))
                    convertView = mLayoutInflater.inflate(R.layout.item_large, null);
                else
                    convertView = mLayoutInflater.inflate(R.layout.item, null);

            }
            holder = new ViewHolder();
            holder.isTextOnlyActive = getPrefs().isTextOnlyActive();
            holder.text = (TextView) convertView.findViewById(R.id.textView);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        
        ImageView imageView = holder.image;
        TextView labelView = holder.text;
        if (imageView != null)
            imageView.setImageDrawable(pkgAppsListShowing.get(position).getIcon());

        labelView.setMaxLines(getPrefs().getMaxLines());

        String label = pkgAppsListShowing.get(position).getLabel();

        int query_index = label.toLowerCase().indexOf(act_query);

        int color = (ctx.getResources()
                .getColor(com.actionbarsherlock.R.color.abs__holo_blue_light));

        String hightlight_label = label;

        if (query_index == -1) { // search not App-Name - hope it is in Package Name - why else we want to show the app?
            label = pkgAppsListShowing.get(position).getPackageName();
            label = label.replace("com.google.android.apps.", "");
            query_index = label.toLowerCase().indexOf(act_query);
        }

        if (query_index != -1) {
            hightlight_label = label.substring(0, query_index)
                    + "<font color='#"
                    + Integer.toHexString(color).toUpperCase().substring(2)
                    + "'>"
                    + label.substring(query_index,
                    query_index + act_query.length())
                    + "</font>"
                    + label.substring(query_index + act_query.length(),
                    label.length());

        }

        labelView.setText(Html.fromHtml(hightlight_label + "<br/><br/>"));

        return convertView;
    }
    
    private static class ViewHolder {
    	public boolean isTextOnlyActive;
        public TextView text;
        public ImageView image;
    }

    public void setActQuery(String act_query) {
        this.act_query = act_query;

        ArrayList<AppInfo> pkgAppsListFilter = new ArrayList<AppInfo>();

        for (AppInfo info : pkgAppsListAll) {
            if (info.getLabel().toLowerCase().contains(act_query)
                    || (getPrefs().isSearchPackageActivated() && (info.getPackageName().toLowerCase().contains(act_query))))

                pkgAppsListFilter.add(info);
        }

        pkgAppsListShowing = pkgAppsListFilter;
        notifyDataSetChanged();
    }

    public AppInfo getAtPosition(int pos) {
        return pkgAppsListShowing.get(pos);
    }

    public FASTPrefs getPrefs() {
        return ((ApplicationContext) ctx.getApplicationContext()).getPrefs();
    }

}