package org.ligi.fast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
/**
 * 
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 */
public class SearchActivity extends SherlockActivity {

	private class AppInfo {
		private String name;
		private BitmapDrawable icon;
		private ResolveInfo ri;

		public AppInfo(ResolveInfo ri) {
			this.ri = ri;
			name = ri.loadLabel(mPackageManager).toString();
		}

		public String getName() {
			return name;
		}

		public Drawable getIcon() {
			if (icon == null) {
				File img_file = new File(getCacheDir() + "/" + ri.activityInfo.packageName + ".png");

				if (!img_file.exists()) {
					Log.i("FAST", "caching image " + img_file.getAbsolutePath());
					try {
						icon = (BitmapDrawable) ri.loadIcon(mPackageManager);
						img_file.createNewFile();
						FileOutputStream fos = new FileOutputStream(img_file);
						icon.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
						fos.close();
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}

				} else {

					try {
						icon = new BitmapDrawable(getResources(), new FileInputStream(img_file));
					} catch (FileNotFoundException e) {
						icon = (BitmapDrawable) ri.loadIcon(mPackageManager);
					}
				}
			}

			return icon;
		}

		public Intent getIntent() {
			Intent intent = new Intent();
			intent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
			return intent;

		}
	}

	private List<AppInfo> pkgAppsListAll;
	private List<AppInfo> pkgAppsListFiltered;

	private LayoutInflater mLayoutInflater;
	private ImageAdapter mAdapter;

	private String act_query = "";
	private PackageManager mPackageManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_search);
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		mPackageManager = getPackageManager();

		pkgAppsListAll = new ArrayList<AppInfo>();

		for (ResolveInfo info : mPackageManager.queryIntentActivities(mainIntent, 0)) {
			pkgAppsListAll.add(new AppInfo(info));
		}

		pkgAppsListFiltered = pkgAppsListAll; // initially we want to show all apps
																					// - no filter

		GridView app_list = (GridView) findViewById(R.id.listView);

		disableOverScoll(app_list);

		mAdapter = new ImageAdapter(this);
		app_list.setAdapter(mAdapter);
		mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME);

		EditText search_et = new EditText(this);
		search_et.setHint("Enter Query");

		search_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

				act_query = s.toString().toLowerCase();

				ArrayList<AppInfo> pkgAppsListFilter = new ArrayList<AppInfo>();

				for (AppInfo info : pkgAppsListAll) {
					if (info.getName().toLowerCase().contains(s.toString().toLowerCase()))
						pkgAppsListFilter.add(info);
				}

				pkgAppsListFiltered = pkgAppsListFilter;
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

		});
		getSupportActionBar().setCustomView(search_et);
		// getSupportActionBar().set

		app_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				try {
					startActivity(pkgAppsListFiltered.get(pos).getIntent());
				} catch (ActivityNotFoundException e) {
					// e.g. uninstalled while app running - TODO should refresh list
				}
			}

		});
		super.onCreate(savedInstanceState);

	}

	@TargetApi(9)
	// we do a check her - all good
	private void disableOverScoll(GridView gridView) {
		if (Build.VERSION.SDK_INT >= 9)
			gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	public class ImageAdapter extends BaseAdapter {

		public ImageAdapter(Context c) {
		}

		public int getCount() {
			return pkgAppsListFiltered.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) { // if it's not recycled, initialize some
				convertView = mLayoutInflater.inflate(R.layout.item, null);

			}

			ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
			TextView labelView = (TextView) convertView.findViewById(R.id.textView);

			imageView.setImageDrawable(pkgAppsListFiltered.get(position).getIcon());

			String label = pkgAppsListFiltered.get(position).getName();

			int query_index = label.toLowerCase().indexOf(act_query);

			int color = (getResources().getColor(com.actionbarsherlock.R.color.abs__holo_blue_light));
			String hightlight_label = label;
			if (query_index != -1) {
				hightlight_label = label.substring(0, query_index) + "<font color='#" + Integer.toHexString(color).toUpperCase().substring(2) + "'>" + label.substring(query_index, query_index + act_query.length()) + "</font>"
						+ label.substring(query_index + act_query.length(), label.length());

			}

			labelView.setText(Html.fromHtml(hightlight_label));

			return convertView;
		}
	}

}
