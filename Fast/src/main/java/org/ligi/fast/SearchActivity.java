package org.ligi.fast;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.ligi.androidhelper.AndroidHelper;
import org.ligi.androidhelper.simplifications.SimpleTextWatcher;
import org.ligi.fast.util.PackageListSerializer;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The main Activity for this App - most things come together here
 */
public class SearchActivity extends Activity {

    private List<AppInfo> pkgAppsListTemp;
    private AppInfoAdapter adapter;
    private File indexFile;
    private String newIndex = "";
    private String oldIndex = "";
    private String oldSearch = "";
    private EditText searchEditText;
    private GridView gridView;
    private String not_load_reason;
    private boolean retry = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.applyTheme(this);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_search);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);

        indexFile = new File(getCacheDir(), "index2.csv");

        try {
            oldIndex = AndroidHelper.at(indexFile).loadToString();
        } catch (Exception e) { // IO ^^
            not_load_reason = e.toString();
            Log.w("could not load new Index:" + not_load_reason);
        }

        pkgAppsListTemp = PackageListSerializer.fromString(this, oldIndex);

        adapter = new AppInfoAdapter(this, pkgAppsListTemp);

        if (App.getSettings().getSortOrder().startsWith("alpha")) {
            adapter.getList().setSortMode(AppInfoList.SortMode.ALPHABETICAL);
        }

        // sync was here

        gridView = (GridView) findViewById(R.id.listView);

        /*getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO
                        | ActionBar.DISPLAY_SHOW_HOME);
          */

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.setSingleLine();
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchEditText.setImeActionLabel("Launch", EditorInfo.IME_ACTION_DONE);

        searchEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (adapter.getCount() > 0) {
                    startItemAtPos(0);
                }
                return true;
            }

        });
        searchEditText.setHint(R.string.query_hint);

        searchEditText.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                boolean was_adding = oldSearch.length() < s.toString().length();
                oldSearch = s.toString().toLowerCase();
                adapter.setActQuery(s.toString().toLowerCase());
                if ((adapter.getCount() == 1) && was_adding && App.getSettings().isLaunchSingleActivated()) {
                    startItemAtPos(0);
                }
            }

        });
        // getSupportActionBar().setCustomView(search_et);

        gridView.setOnItemClickListener(new OnItemClickListener() {

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

        gridView.setLongClickable(true);

        gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long arg3) {
                new AppActionDialogBuilder(SearchActivity.this, adapter.getList().get(pos)).show();
                return true;
            }

        });

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);

        if (pkgAppsListTemp.size() == 0) {
            startActivityForResult(new Intent(this, LoadingDialog.class), R.id.activityResultLoadingDialog);
        } else { // the second time - we use the old index to be fast but
            // regenerate in background to be recent

            pkgAppsListTemp = new ArrayList<AppInfo>();

            new BaseAppGatherAsyncTask(this) {

                @Override
                protected void onProgressUpdate(AppInfo... values) {
                    super.onProgressUpdate(values);
                    pkgAppsListTemp.add(values[0]);
                    newIndex += values[0].toCacheString() + "\n";
                    retry = false;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    if (!retry) {
                        process_new_index();
                    }
                }

            }.execute();
        }
    }

    public void startItemAtPos(int pos) {
        Intent intent = adapter.getList().get(pos).getIntent();
        intent.setAction("android.intent.action.MAIN");
        // set flag so that next start the search app comes up and not the last started App
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * takes the temp apps list as the new all apps index
     */
    private void process_new_index() {

        if (!newIndex.equals(oldIndex)) {
            Log.i("processing new app-index");
            // TODO we should do a cleanup of cached icons here regarding the new index
            adapter.getList().setAppsList(pkgAppsListTemp);

            try {
                FileOutputStream fos = new FileOutputStream(indexFile);
                fos.write(newIndex.getBytes());
                fos.close();
            } catch (IOException e) {
                Log.i("could not write new index because " + e
                        + " user might suffer from constant index rebuilds");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case R.id.activityResultLoadingDialog:
                if (data != null) {
                    newIndex = data.getStringExtra("newIndex");
                    pkgAppsListTemp = PackageListSerializer.fromString(this, newIndex);
                    process_new_index();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        searchEditText.setText(""); // using the app showed that we want a new search here and the old stuff is not interesting anymore

        searchEditText.requestFocus();

        // workaround from http://code.google.com/p/android/issues/detail?id=3612
        searchEditText.postDelayed(new Runnable() {

            @Override
            public void run() {
                AndroidHelper.at(searchEditText).hideKeyBoard();
            }
        }, 200);

        gridView.setAdapter(adapter);

        String iconSize=App.getSettings().getIconSize();
        if (iconSize.equals("tiny")) {
            gridView.setColumnWidth((int) this.getResources().getDimension(R.dimen.cell_size_tiny));
        } else if (iconSize.equals("small")) {
            gridView.setColumnWidth((int) this.getResources().getDimension(R.dimen.cell_size_small));
        } else if (iconSize.equals("large")) {
            gridView.setColumnWidth((int) this.getResources().getDimension(R.dimen.cell_size_large));
        } else {
            gridView.setColumnWidth((int) this.getResources().getDimension(R.dimen.cell_size));
        }

    }

    @SuppressWarnings("UnusedDeclaration") // the API is that way
    public void settingsClicked(View v) {
        startActivity(new Intent(this, FASTPrefsActivity.class));
        finish();
    }

    public void helpClicked(View v) {
        HelpDialog.show(this);
    }

}
