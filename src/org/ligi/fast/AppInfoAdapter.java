package org.ligi.fast;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Adapter to provide the AppInfo to the user - also care for the filtering of the query
 * 
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 */
public class AppInfoAdapter extends BaseAdapter {

	private Context ctx;

	private List<AppInfo> pkgAppsListAll;
	private List<AppInfo> pkgAppsListShowing;

	private String act_query = "";

	public AppInfoAdapter(Context _ctx,List<AppInfo> _pkgAppsListAll) {
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

		if (convertView == null) { // if it's not recycled, initialize some
			LayoutInflater mLayoutInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mLayoutInflater.inflate(R.layout.item, null);

		}

		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageView);
		TextView labelView = (TextView) convertView.findViewById(R.id.textView);

		imageView.setImageDrawable(pkgAppsListShowing.get(position).getIcon());

		String label = pkgAppsListShowing.get(position).getLabel();

		int query_index = label.toLowerCase().indexOf(act_query);

		int color = (ctx.getResources()
				.getColor(com.actionbarsherlock.R.color.abs__holo_blue_light));
		String hightlight_label = label;
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

		labelView.setText(Html.fromHtml(hightlight_label));

		return convertView;
	}

	public void setActQuery(String act_query) {
		this.act_query=act_query;
		
		ArrayList<AppInfo> pkgAppsListFilter = new ArrayList<AppInfo>();

		for (AppInfo info : pkgAppsListAll) {
			if (info.getLabel().toLowerCase().contains(act_query))
				pkgAppsListFilter.add(info);
		}

		pkgAppsListShowing = pkgAppsListFilter;
		notifyDataSetChanged();
	}
	
		
	public AppInfo getAtPosition(int pos) {
		return pkgAppsListShowing.get(pos);
	}
}