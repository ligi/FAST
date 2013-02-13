package org.ligi.fast;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;

/**
 * Activity to Edit the Preferences
 *
 * @author Marcus -ligi- Büschleb
 *         <p/>
 *         License GPLv3
 */
public class FASTPrefsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ApplicationContext) getApplicationContext()).applyTheme(this);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        super.onCreate(savedInstanceState);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreferenceScreen prefs_screen = createPreferenceHierarchy();
        setPreferenceScreen(prefs_screen);


        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.prefs_title);
    }

    private PreferenceScreen createPreferenceHierarchy() {
        @SuppressWarnings("deprecation")
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
                this);

        CheckBoxPreference doLaunchSingleCheckBox = new CheckBoxPreference(this);
        doLaunchSingleCheckBox.setKey(FASTPrefs.KEY_LAUNCHSINGLE);
        doLaunchSingleCheckBox.setTitle(R.string.launch_single);
        doLaunchSingleCheckBox.setSummary(R.string.auto_launch_if_only_one_app_left);
        doLaunchSingleCheckBox.setDefaultValue(false);

        CheckBoxPreference doSearchInPackage = new CheckBoxPreference(this);
        doSearchInPackage.setKey(FASTPrefs.KEY_SEARCHPKG);
        doSearchInPackage.setTitle(R.string.search_in_package);
        doSearchInPackage.setSummary(R.string.also_use_the_package_name_for_searching);
        doSearchInPackage.setDefaultValue(false);

        CheckBoxPreference marketForAllApps = new CheckBoxPreference(this);
        marketForAllApps.setKey(FASTPrefs.KEY_MARKETFORALL);
        marketForAllApps.setTitle("" + String.format(getResources().getString(R.string.open_in_for_all), ApplicationContext.STORE_NAME));
        marketForAllApps.setSummary(R.string.even_if_installed_another_way);
        marketForAllApps.setDefaultValue(false);

        CheckBoxPreference textOnly = new CheckBoxPreference(this);
        textOnly.setKey(FASTPrefs.KEY_TEXTONLY);
        textOnly.setTitle(R.string.text_only);
        textOnly.setSummary(R.string.show_no_icons_pure_text);
        textOnly.setDefaultValue(false);

        ListPreference maxLinesPref = new ListPreference(this);
        maxLinesPref.setKey(FASTPrefs.KEY_MAXLINES);
        maxLinesPref.setTitle(R.string.max_text_lines);
        maxLinesPref.setSummary(R.string.how_much_text_you_want);
        maxLinesPref.setEntries(new CharSequence[]{"1", "2", "3"});
        maxLinesPref.setEntryValues(new CharSequence[]{"1", "2", "3"});
        maxLinesPref.setDefaultValue("1");

        ListPreference iconSizePref = new ListPreference(this);
        iconSizePref.setKey(FASTPrefs.KEY_ICONSIZE);
        iconSizePref.setTitle(R.string.icon_size);
        iconSizePref.setSummary(R.string.how_big_icons);
        iconSizePref.setEntries(R.array.sizes);
        iconSizePref.setEntryValues(new CharSequence[]{"small", "medium", "large"});
        iconSizePref.setDefaultValue("medium");

        ListPreference sortPref = new ListPreference(this);
        sortPref.setKey(FASTPrefs.KEY_SORT);
        sortPref.setTitle(getString(R.string.sort));
        sortPref.setSummary(getString(R.string.sort_decr));
        sortPref.setEntries(R.array.sort_orders);
        sortPref.setEntryValues(new CharSequence[]{"unsorted", "alpha"});
        sortPref.setDefaultValue("unsorted");

        ListPreference themePref = new ListPreference(this);
        themePref.setKey(FASTPrefs.KEY_THEME);
        themePref.setTitle(R.string.theme);
        themePref.setSummary(R.string.choose_your_look);
        themePref.setEntries(R.array.themes);
        themePref.setEntryValues(new CharSequence[]{"dark", "light", "transparent", "transparent_light"});
        themePref.setDefaultValue("dark");

        root.addPreference(themePref);
        root.addPreference(iconSizePref);
        root.addPreference(maxLinesPref);
        root.addPreference(doLaunchSingleCheckBox);
        root.addPreference(doSearchInPackage);
        root.addPreference(marketForAllApps);
        root.addPreference(textOnly);
        root.addPreference(sortPref);

        return root;
    }

    @Override
    public void onBackPressed() {
        // this workaround is needed to apply the theme
        finish();
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void homePressed(View v) {
        onBackPressed();
    }

    public void shareClicked(View v) {
        String message = "Launch Android Apps really FAST: " + ApplicationContext.getStoreURL4PackageName("id=org.ligi.fast");
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, "Share FAST"));
    }

    public void helpClicked(View v) {
        HelpDialog.show(this);
    }
}
