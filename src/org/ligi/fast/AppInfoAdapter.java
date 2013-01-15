package org.ligi.fast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

    private static List<AppInfo> pkgAppsListShowing;
    private static int firstTimeLoading = 0;
	
    private Context ctx;
    private List<AppInfo> pkgAppsListAll;
    private String act_query = "";
    private String colorString = "";

    public AppInfoAdapter(Context _ctx, List<AppInfo> _pkgAppsListAll) {
        ctx = _ctx;
        setAllAppsList(_pkgAppsListAll);
    }

    public void setAllAppsList(List<AppInfo> _pkgAppsListAll) {
        pkgAppsListAll = new ArrayList<AppInfo>();
        pkgAppsListAll.addAll(_pkgAppsListAll);
        setActQuery(act_query); // to rebuild the showing list
        
        int color = (ctx.getResources()
                .getColor(com.actionbarsherlock.R.color.abs__holo_blue_light));
        
        colorString = Integer.toHexString(color).toUpperCase().substring(2);
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
        holder.position = position;
        
        ImageView imageView = holder.image;
        TextView labelView = holder.text;
        if (imageView != null) {
        	if (position == 1)
    			firstTimeLoading += 1;
        	if (firstTimeLoading < 3) {
        		
        		Drawable drawable = pkgAppsListShowing.get(position).getIcon();
        		holder.image.setImageDrawable(drawable);
        	} else {
        		new IconTask(position, holder).execute(this);
        	}
        }

        labelView.setMaxLines(getPrefs().getMaxLines());

        String label = pkgAppsListShowing.get(position).getLabel();
        String hightlight_label = label;
        
        int query_index = label.toLowerCase().indexOf(act_query);

        if (act_query.length() == 0) {
        	labelView.setText(Html.fromHtml(label + "<br/><br/>"));
        	return convertView;
        }
        
        if (query_index == -1) { // search not App-Name - hope it is in Package Name - why else we want to show the app?
            label = pkgAppsListShowing.get(position).getPackageName();
            label = label.replace("com.google.android.apps.", "");
            query_index = label.toLowerCase().indexOf(act_query);
        } 
        
        if (query_index != -1 ) {
            hightlight_label = label.substring(0, query_index)
                    + "<font color='#"
                    + colorString
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
    	int position;
    	
    	public boolean isTextOnlyActive;
        public TextView text;
        public ImageView image;
    }
    
    private static class IconTask extends AsyncTask<AppInfoAdapter, Void, Drawable> {
        private int mPosition;
        private ViewHolder mHolder;
        private AppInfoAdapter mAdapter;

        public IconTask(int position, ViewHolder holder) {
            mPosition = position;
            mHolder = holder;
        }

        protected Drawable doInBackground(AppInfoAdapter... params) {
        	mAdapter = params[0];
			return pkgAppsListShowing.get(mPosition).getIcon();
        }

        protected void onPostExecute(Drawable drawable) {
            if (mHolder.position == mPosition) {
            	mHolder.image.setImageDrawable(drawable);
            }
        }
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