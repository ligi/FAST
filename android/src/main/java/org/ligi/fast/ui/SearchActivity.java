package org.ligi.fast.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.ligi.axt.helpers.ViewHelper;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.fast.App;
import org.ligi.fast.R;
import org.ligi.fast.background.BackgroundGatherAsyncTask;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.model.DynamicAppInfoList;
import org.ligi.fast.util.AppInfoListStore;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;
import java.util.Locale;

/**
 * The main Activity for this App - most things come together here
 */
public class SearchActivity extends Activity implements App.PackageChangedListener {

    //private List<AppInfo> pkgAppsListTemp;
    private DynamicAppInfoList appInfoList;
    private AppInfoAdapter adapter;
    private String oldSearch = "";
    private EditText searchQueryEditText;
    private GridView gridView;

    private AppInfoListStore appInfoListStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.applyTheme(this);

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_search);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);

        appInfoListStore = new AppInfoListStore(this);

        appInfoList = new DynamicAppInfoList(appInfoListStore.load(),App.getSettings());

        adapter = new AppInfoAdapter(this, appInfoList);

        configureAdapter();

        gridView = (GridView) findViewById(R.id.listView);

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
                final String editString = s.toString();
                final boolean was_adding = oldSearch.length() < editString.length();
                oldSearch = editString.toLowerCase(Locale.ENGLISH);
                adapter.setActQuery(editString.toLowerCase(Locale.ENGLISH));
                startAppWhenItItIsTheOnlyOneInList(was_adding);
            }

        });

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                try {
                    startItemAtPos(pos);
                } catch (ActivityNotFoundException e) {
                    // e.g. uninstalled while app running - TODO should refresh list
                }
            }

        });

        gridView.setLongClickable(true);

        gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long arg3) {
                new AppActionDialogBuilder(SearchActivity.this, adapter.getItem(pos)).show();
                return true;
            }

        });

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);


        if (appInfoList.size() == 0) {
            startActivityForResult(new Intent(this, LoadingDialog.class), R.id.activityResultLoadingDialog);
        } else { // the second time - we use the old index to be fast but
            // regenerate in background to be recent

            // Use the pkgAppsListTemp in order to update data from the saved file with recent
            // call count information (seeing as we may not have saved it recently).
            new BackgroundGatherAsyncTask(this, appInfoList).execute();
        }

        gridView.setAdapter(adapter);
    }

    private void startAppWhenItItIsTheOnlyOneInList(boolean was_adding) {
        if ((adapter.getCount() == 1) && was_adding && App.getSettings().isLaunchSingleActivated()) {
            startItemAtPos(0);
        }
    }

    private void configureAdapter() {
        if (App.getSettings().getSortOrder().startsWith("alpha")) {
            adapter.setSortMode(DynamicAppInfoList.SortMode.ALPHABETICAL);
        } else if (App.getSettings().getSortOrder().equals("most_used")) {
            adapter.setSortMode(DynamicAppInfoList.SortMode.MOST_USED);
        }
    }

    public void startItemAtPos(int pos) {
		AppInfo app = adapter.getItem(pos);
		app.incrementCallCount();
        Intent intent = app.getIntent();
        intent.setAction(Intent.ACTION_MAIN);
        // set flag so that next start the search app comes up and not the last started App
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(App.LOG_TAG, "Starting " + app.getActivityName() + " (and incremented call count to " + app.getCallCount() + ")");
        startActivity(intent);

        if (Build.VERSION.SDK_INT>18) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchQueryEditText.getWindowToken(), 0);
        }

        if (App.getSettings().isFinishOnLaunchEnabled()) {
            finish();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case R.id.activityResultLoadingDialog:
                onPackageChange(appInfoListStore.load());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        App.packageChangedListener = this;

        searchQueryEditText.setText(""); // using the app showed that we want a new search here and the old stuff is not interesting anymore

        dealWithUserPreferencesRegardingSoftKeyboard();

        configureAdapter();

        final String iconSize = App.getSettings().getIconSize();
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
                new ViewHelper(searchQueryEditText).showKeyboard();
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
    public void onPackageChange(final AppInfoList newAppInfoList) {
        // TODO we should also do a cleanup of cached icons here
        // we might not come from UI Thread

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateList(newAppInfoList);
            }
        });
    }

    @Override
    protected void onPause() {
        App.packageChangedListener = null;
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Need to persist the call count values, or else the sort by "most used"
        // will not work next time we open this activity.
        //appInfoListStore.save(adapter);
        super.onStop();
    }

}
