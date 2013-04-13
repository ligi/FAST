package org.ligi.fast;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.ligi.fast.util.FileHelper;
import org.ligi.tracedroid.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The main Activity for this App
 *
 * @author Marcus -ligi- Büschleb
 *         <p/>
 *         License GPLv3
 */
public class SearchActivity extends Activity {

    private List<AppInfo> pkgAppsListTemp;
    private AppInfoAdapter mAdapter;
    private File index_file;
    private String new_index = "";
    private String old_index = "";
    private String old_search = "";
    private EditText search_et;
    private GridView mGridView;
    private String not_load_reason;
    private boolean retry = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ApplicationContext) getApplicationContext()).applyTheme(this);


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_search);


        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);

        pkgAppsListTemp = new ArrayList<AppInfo>();

        index_file = new File(getCacheDir(), "index2.csv");

        try {
            old_index = FileHelper.file2String(index_file);
            String[] lines = old_index.split("\n");
            for (String line : lines) {
                if (line.length() > 0)
                    pkgAppsListTemp.add(new AppInfo(this, line));
            }
            Log.i("act index " + old_index);

        } catch (Exception e) {
            not_load_reason = e.toString();
            Log.w("could not load new Index: " + not_load_reason);
        }

        mAdapter = new AppInfoAdapter(this, pkgAppsListTemp);

        if (getPrefs().getSortOrder().startsWith("alpha"))
            mAdapter.setSortMode(AppInfoAdapter.SortMode.ALPHABETICAL);

        // sync was here

        if (pkgAppsListTemp.size() == 0)
            new BaseAppGatherAsyncTask(this) {

                private LoadingDialog mLoadingDialog;

                @Override
                protected void onPreExecute() {
                    mLoadingDialog = new LoadingDialog(SearchActivity.this);
                    mLoadingDialog.setTitle("Nolo" + not_load_reason);
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

            pkgAppsListTemp = new ArrayList<AppInfo>();

            new BaseAppGatherAsyncTask(this) {

                @Override
                protected void onProgressUpdate(AppInfo... values) {
                    super.onProgressUpdate(values);
                    pkgAppsListTemp.add(values[0]);
                    new_index += values[0].toCacheString() + "\n";
                    retry = false;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    if (retry == false) process_new_index();
                }

            }.execute();
        }

        mGridView = (GridView) findViewById(R.id.listView);

        /*getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO
                        | ActionBar.DISPLAY_SHOW_HOME);
          */

        search_et = (EditText) findViewById(R.id.searchEditText);
        search_et.setSingleLine();
        search_et.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
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
                boolean was_adding = old_search.length() < s.toString().length();
                old_search = s.toString().toLowerCase();
                mAdapter.setActQuery(s.toString().toLowerCase());
                if ((mAdapter.getCount() == 1) && was_adding && getPrefs().isLaunchSingleActivated()) {
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
        // getSupportActionBar().setCustomView(search_et);

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
                new AppActionDialogBuilder(SearchActivity.this, mAdapter.getAtPosition(pos)).show();
                return true;
            }

        });

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);

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
            Log.i("processing new app-index");
            // TODO we should do a cleanup of cached icons here regarding the new index
            mAdapter.setAllAppsList(pkgAppsListTemp);
            try {
                FileOutputStream fos = new FileOutputStream(index_file);
                fos.write(new_index.getBytes());
                fos.close();
            } catch (IOException e) {

            }
        }
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
        }, 200);

        Log.i("Resume with " + getPrefs().isTextOnlyActive());
        mGridView.setAdapter(mAdapter);

        if (new FASTPrefs(this).getIconSize().equals("small"))
            mGridView.setColumnWidth((int) this.getResources().getDimension(R.dimen.cell_size_small));
        else if (new FASTPrefs(this).getIconSize().equals("large"))
            mGridView.setColumnWidth((int) this.getResources().getDimension(R.dimen.cell_size_large));
        else
            mGridView.setColumnWidth((int) this.getResources().getDimension(R.dimen.cell_size));

    }

    public FASTPrefs getPrefs() {
        return ((ApplicationContext) getApplicationContext()).getPrefs();
    }

    public void settingsClicked(View v) {
        startActivity(new Intent(this, FASTPrefsActivity.class));
        finish();
    }

    public void helpClicked(View v) {
        HelpDialog.show(this);
    }

}
