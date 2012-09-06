package org.ligi.fast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ligi.fast.util.FileHelper;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * The main Activity for this App
 * 
 * @author Marcus -ligi- Büschleb
 * 
 *         License GPLv3
 */
public class SearchActivity extends SherlockActivity {

	private List<AppInfo> pkgAppsListTemp;

	private AppInfoAdapter mAdapter;

	private File index_file;
	private String new_index = "";
	private String old_index = "";
	private String old_search = "";
	private EditText search_et;
	private GridView mGridView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search);
		
		pkgAppsListTemp = new ArrayList<AppInfo>();

		index_file = new File(getCacheDir(), "index2.csv");

		
		try {
			old_index = FileHelper.file2String(index_file);
			String[] lines = old_index.split("\n");
			for (String line : lines) {
				if (line.length() > 0)
					pkgAppsListTemp.add(new AppInfo(this, line));
			}
			Log.i("FAST" , "act index  " + old_index);
			
		} catch (Exception e) {

		}

		mAdapter = new AppInfoAdapter(this, pkgAppsListTemp);		
		// sync was here
		
		if (pkgAppsListTemp.size() == 0)
			new BaseAppGatherAsyncTask(this) {
	
				private LoadingDialog mLoadingDialog;
	
				@Override
				protected void onPreExecute() {
					mLoadingDialog = new LoadingDialog(SearchActivity.this);
					mLoadingDialog.show();
				}
	
				@Override
				protected void onProgressUpdate(AppInfo... values) {
					super.onProgressUpdate(values);
	
					mLoadingDialog.setIcon(values[0].getIcon());
					mLoadingDialog.setText(values[0].getLabel());
	
					pkgAppsListTemp.add(values[0]);
					new_index += values[0].toCacheString() + "\n";
				}
	
				@Override
				protected void onPostExecute(Void result) {
					mLoadingDialog.dismiss();
					super.onPostExecute(result);
					process_new_index();
				}
	
			}.execute();
		else { // the second time - we use the old index to be fast but
				// regenerate in background to be recent
			
			pkgAppsListTemp=new ArrayList<AppInfo>();
			
			new BaseAppGatherAsyncTask(this) {
	
				@Override
				protected void onProgressUpdate(AppInfo... values) {
					super.onProgressUpdate(values);
					pkgAppsListTemp.add(values[0]);
					new_index += values[0].toCacheString() + "\n";
				}
	
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					process_new_index();
				}
	
			}.execute();
		}
		
		mGridView = (GridView) findViewById(R.id.listView);

		disableOverScoll(mGridView);

		//mGridView.setAdapter(mAdapter);

		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO
						| ActionBar.DISPLAY_SHOW_HOME);

		search_et = new EditText(this);
		search_et.setSingleLine();
		search_et.setImeOptions(EditorInfo.IME_ACTION_DONE);
		search_et.setImeActionLabel("Launch", EditorInfo.IME_ACTION_DONE);
		
		search_et.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				startItemAtPos(0);
				return true;
			}
			
		});
		search_et.setHint(R.string.query_hint);

		search_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				boolean was_adding=old_search.length()<s.toString().length();
				old_search=s.toString().toLowerCase();
				mAdapter.setActQuery(s.toString().toLowerCase());
				if ((mAdapter.getCount()==1)&&was_adding&&getPrefs().isLaunchSingleActivated()) {
					startItemAtPos(0);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});
		getSupportActionBar().setCustomView(search_et);
		// getSupportActionBar().set

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				try {
					startItemAtPos(pos);
				} catch (ActivityNotFoundException e) {
					// e.g. uninstalled while app running - TODO should refresh
					// list
				}
			}

		});
		
		mGridView.setLongClickable(true);
				
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				new AppActionDialogBuilder(SearchActivity.this,mAdapter.getAtPosition(pos)).show();
				return true;
			}
			
		});

		
		
	}
	
	public void startItemAtPos(int pos) {
		Intent intent = mAdapter.getAtPosition(pos).getIntent();
		intent.setAction("android.intent.action.MAIN");
		// set flag so that next start the search app comes up and not the last started App
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	

	/**
	 * takes the temp apps list as the new all apps index
	 */
	private void process_new_index() {

		if (!new_index.equals(old_index)) {
			Log.i("FastAppSearchTool", "processing new app-index");
			// TODO we should do a cleanup of cached icons here regarding the new index
			mAdapter.setAllAppsList(pkgAppsListTemp);
			try {
				FileOutputStream fos = new FileOutputStream(index_file);
				fos.write(new_index.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {

			} catch (IOException e) {

			}
		}
	}

	@TargetApi(9)
	// we do a check for SDK Version here - all good
	private void disableOverScoll(GridView gridView) {
		if (Build.VERSION.SDK_INT >= 9)
			gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this,FASTPrefsActivity.class));
			return true;
			
		case R.id.menu_rate:
			String myUrl ="https://play.google.com/store/apps/details?id=" + this.getPackageName();

			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myUrl)));
			return true;
			
		case R.id.menu_share:
			String message = "Launch Android Apps really FAST: https://play.google.com/store/apps/details?id=org.ligi.fast";
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, message);

			startActivity(Intent.createChooser(share, "Share FAST"));
			return true;
			
		case R.id.menu_connect:
			String myPlusUrl ="http://plus.google.com/108684152853280523320";

			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myPlusUrl)));
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		search_et.setText(""); // using the app showed that we want a new search here and the old stuff is not interesting anymore 
		
		search_et.requestFocus();
		
		// workaround from http://code.google.com/p/android/issues/detail?id=3612
		search_et.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputMethodManager keyboard = (InputMethodManager)
				getSystemService(Context.INPUT_METHOD_SERVICE);

				keyboard.showSoftInput(search_et, 0);
			}
		},200);

		Log.i("FAST","Resume with " + getPrefs().isTextOnlyActive());
		mGridView.setAdapter(mAdapter);
	}
	
	public FASTPrefs getPrefs() {
		return ((ApplicationContext)getApplicationContext()).getPrefs();
	}
	
}
