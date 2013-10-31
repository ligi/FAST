package org.ligi.fast.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.ligi.axt.AXT;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.fast.App;
import org.ligi.fast.R;
import org.ligi.fast.background.BackgroundGatherAsyncTask;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.util.PackageListStore;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.util.ArrayList;
import java.util.List;

/**
 * The main Activity for this App - most things come together here
 */
public class SearchActivity extends Activity implements App.PackageChangedListener {

    private List<AppInfo> pkgAppsListTemp;
    private AppInfoAdapter adapter;
    private String oldSearch = "";
    private EditText searchQueryEditText;
    private GridView gridView;

    private PackageListStore packageListStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.applyTheme(this);

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_search);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);

        packageListStore = new PackageListStore(this);

        pkgAppsListTemp = packageListStore.load();

        adapter = new AppInfoAdapter(this, pkgAppsListTemp);

        configureAdapter();

        gridView = (GridView) findViewById(R.id.listView);

        /*getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO
                        | ActionBar.DISPLAY_SHOW_HOME);
          */

        searchQueryEditText = (EditText) findViewById(R.id.searchEditText);
        searchQueryEditText.setSingleLine();
        searchQueryEditText.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchQueryEditText.setImeActionLabel("Launch", EditorInfo.IME_ACTION_DONE);

        searchQueryEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (adapter.getCount() > 0) {
                    startItemAtPos(0);
                }
                return true;
            }

        });
        searchQueryEditText.setHint(R.string.query_hint);

        searchQueryEditText.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                boolean was_adding = oldSearch.length() < s.toString().length();
                oldSearch = s.toString().toLowerCase();
                adapter.setActQuery(s.toString().toLowerCase());
                startAppWhenItIstheOnlyOneInList(was_adding);
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

            new BackgroundGatherAsyncTask(this).execute();
        }
    }

    private void startAppWhenItIstheOnlyOneInList(boolean was_adding) {
        if ((adapter.getCount() == 1) && was_adding && App.getSettings().isLaunchSingleActivated()) {
            startItemAtPos(0);
        }
    }

    private void configureAdapter() {
        if (App.getSettings().getSortOrder().startsWith("alpha")) {
            adapter.getList().setSortMode(AppInfoList.SortMode.ALPHABETICAL);
        }
    }

    public void startItemAtPos(int pos) {
        Intent intent = adapter.getList().get(pos).getIntent();
        intent.setAction("android.intent.action.MAIN");
        // set flag so that next start the search app comes up and not the last started App
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        if (App.getSettings().isFinishOnLaunchEnabled()) {
            finish();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case R.id.activityResultLoadingDialog:
                onPackageChange();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        App.packageChangedListener = this;

        searchQueryEditText.setText(""); // using the app showed that we want a new search here and the old stuff is not interesting anymore

        dealWithUserPreferencesRegardingSoftKeyboard();

        gridView.setAdapter(adapter);

        String iconSize = App.getSettings().getIconSize();
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

    private void dealWithUserPreferencesRegardingSoftKeyboard() {
        if (App.getSettings().isShowKeyBoardOnStartActivated()) {
            showKeyboard();
        } else {
            hideKeyboard();
        }

    }

    private void hideKeyboard() {
        searchQueryEditText.clearFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void showKeyboard() {
        searchQueryEditText.requestFocus();

        searchQueryEditText.postDelayed(new Runnable() {

            @Override
            public void run() {
                AXT.at(searchQueryEditText).showKeyboard();
            }
        }, 200);
    }

    @SuppressWarnings("UnusedDeclaration") // the API is that way
    public void settingsClicked(View v) {
        startActivity(new Intent(this, FASTSettingsActivity.class));
        finish();
    }

    @SuppressWarnings("UnusedDeclaration") // the API is that way
    public void helpClicked(View v) {
        HelpDialog.show(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        finishIfWeAreNotTheDefaultLauncher();

    }

    private void finishIfWeAreNotTheDefaultLauncher() {
        // TODO check if we perhaps only want this when we have only one launcher
        if (!getPackageName().equals(getHomePackageName())) {
            finish();
        }
    }

    private String getHomePackageName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @Override
    public void onPackageChange() {
        // TODO we should also do a cleanup of cached icons here
        // we might not come from UI Thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter = new AppInfoAdapter(SearchActivity.this, packageListStore.load());
                configureAdapter();
                adapter.setActQuery(searchQueryEditText.getText().toString().toLowerCase());
                gridView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onPause() {
        App.packageChangedListener = null;
        super.onPause();
    }

}
