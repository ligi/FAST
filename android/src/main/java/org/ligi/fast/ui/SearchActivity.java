package org.ligi.fast.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ChangedPackages;
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
import android.widget.Toast;

import org.ligi.axt.helpers.ViewHelper;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.fast.App;
import org.ligi.fast.R;
import org.ligi.fast.background.AppInstallOrRemoveReceiver;
import org.ligi.fast.background.BackgroundGatherAsyncTask;
import org.ligi.fast.background.ChangedPackagesAsyncTask;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.model.DynamicAppInfoList;
import org.ligi.fast.util.AppInfoListStore;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The main Activity for this App - most things come together here
 */
public class SearchActivity extends Activity implements App.PackageChangedListener {

    private AppInstallOrRemoveReceiver mAppReceiver;
    private DynamicAppInfoList appInfoList;
    private AppInfoAdapter adapter;
    private String oldSearch = "";
    private EditText searchQueryEditText;
    private GridView gridView;
    private Context mContext;

    private AppInfoListStore appInfoListStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.applyTheme(this);

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_search);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);

        appInfoListStore = new AppInfoListStore(this);

        final AppInfoList loadedAppInfoList = appInfoListStore.load();
        appInfoList = new DynamicAppInfoList(loadedAppInfoList, App.getSettings());

        adapter = new AppInfoAdapter(this, appInfoList);
        App.backingAppInfoList = new WeakReference<>(appInfoList.getBackingAppInfoList());

        mContext = this;
        // Although this will also be done in onResume(), it is not guaranteed
        // that it happens before the ChangedPackagesAsyncTask will try to save
        App.packageChangedListener = this;

        // Since on Android 8 Oreo the BroadcastReceiver won't run while FAST itself not running,
        // update the App List as needed now at app start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int lastKnownSequenceNumber = App.getSettings().getSequenceNumber();
            if (lastKnownSequenceNumber == App.getSettings().DEFAULT_KNOWN_PM_SEQUENCE_NUMBER) {
                // First launch of FAST after boot
                // Since changes between last app shutdown and device shutdown could be possible and
                // won't be returned by the package manager anymore there's a full refresh needed.
                App.getSettings().putSequenceNumber(0);
                new BackgroundGatherAsyncTask(getApplicationContext()).execute();
            } else {
                ChangedPackages changedPackages =
                        getApplicationContext().getPackageManager()
                                .getChangedPackages(lastKnownSequenceNumber);
                if (changedPackages != null) {
                    App.getSettings().putSequenceNumber(changedPackages.getSequenceNumber());
                    new ChangedPackagesAsyncTask(getApplicationContext(), changedPackages.getPackageNames()).execute();
                }
            }
        }

        // Since starting at API level 26 Oreo most implicit Broadcasts can't be
        // registered in the Manifest anymore, register to them now at app start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Register App Install Receiver
            mAppReceiver = new AppInstallOrRemoveReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addDataScheme("package");
            mContext.registerReceiver(mAppReceiver, filter);
        }

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
                startItemAtPos(pos);
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
        }

        gridView.setAdapter(adapter);
    }

    private void startAppWhenItItIsTheOnlyOneInList(boolean was_adding) {
        if ((adapter.getCount() == 1) && was_adding && App.getSettings().isLaunchSingleActivated()) {
            startItemAtPos(0);
        }
    }

    public void configureAdapter() {
        if (App.getSettings().getSortOrder().startsWith("alpha")) {
            adapter.setSortMode(DynamicAppInfoList.SortMode.ALPHABETICAL);
        } else if (App.getSettings().getSortOrder().equals("most_used")) {
            adapter.setSortMode(DynamicAppInfoList.SortMode.MOST_USED);
        } else if (App.getSettings().getSortOrder().equals("last_installed")) {
            adapter.setSortMode(DynamicAppInfoList.SortMode.LAST_INSTALLED);
        } else {
            adapter.setSortMode(DynamicAppInfoList.SortMode.UNSORTED);
        }
    }

    public void startItemAtPos(int pos) {
        final AppInfo app = adapter.getItem(pos);
        app.incrementCallCount();
        final Intent intent = app.getIntent();
        intent.setAction(Intent.ACTION_MAIN);
        // set flag so that next start the search app comes up and not the last started App
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        Log.d(App.LOG_TAG, "Starting " + app.getActivityName() + " (and incremented call count to " + app.getCallCount() + ")");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "\"" + app.getDisplayLabel() + "\"\nis no longer there - Refreshing data...", Toast.LENGTH_LONG).show();
            List<String> packages = new ArrayList<>();
            packages.add(app.getPackageName());
            new ChangedPackagesAsyncTask(getApplicationContext(), packages).execute();
        }

        if (Build.VERSION.SDK_INT > 18) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchQueryEditText.getWindowToken(), 0);
        }

        if (App.getSettings().isFinishOnLaunchEnabled()) {
            Log.i(App.LOG_TAG, "Finished early.");
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

        gridView.setColumnWidth((int) getResources().getDimension(getWidthByIconSize(iconSize)));
    }

    private static int getWidthByIconSize(String iconSize) {
        switch (iconSize) {
            case "tiny":
                return R.dimen.cell_size_tiny;
            case "small":
                return R.dimen.cell_size_small;
            case "large":
                return R.dimen.cell_size_large;
            default:
                return R.dimen.cell_size;
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
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @Override
    public void onPackageChange(final AppInfoList newAppInfoList) {
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
        // Need to persist the call count values, or else the sort by "most used"
        // will not work next time we open this activity.
        appInfoListStore.save(appInfoList.getBackingAppInfoList());

        App.packageChangedListener = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.backingAppInfoList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mContext.unregisterReceiver(mAppReceiver);
    }

    public void addEntry(AppInfo new_entry) {
        appInfoList.getBackingAppInfoList().add(new_entry);
    }

    public void removeEntry(AppInfo entry) {
        appInfoList.getBackingAppInfoList().remove(entry);
    }
}
