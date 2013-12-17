package org.ligi.fast.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;

import org.ligi.axt.helpers.FileHelper;
import org.ligi.fast.App;
import org.ligi.fast.R;
import org.ligi.fast.TargetStore;
import org.ligi.fast.settings.FASTSettings;

/**
 * Activity to Edit the Preferences
 */
public class FASTSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.applyTheme(this);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        super.onCreate(savedInstanceState);

        PreferenceScreen prefs_screen = createPreferenceHierarchy();
        setPreferenceScreen(prefs_screen);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.prefs_title);
    }

    private PreferenceScreen createPreferenceHierarchy() {
        @SuppressWarnings("deprecation")
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
                this);

        CheckBoxPreference doLaunchSingleCheckBox = new CheckBoxPreference(this);
        doLaunchSingleCheckBox.setKey(FASTSettings.KEY_LAUNCHSINGLE);
        doLaunchSingleCheckBox.setTitle(R.string.launch_single);
        doLaunchSingleCheckBox.setSummary(R.string.auto_launch_if_only_one_app_left);
        doLaunchSingleCheckBox.setDefaultValue(false);

        CheckBoxPreference doSearchInPackage = new CheckBoxPreference(this);
        doSearchInPackage.setKey(FASTSettings.KEY_SEARCHPKG);
        doSearchInPackage.setTitle(R.string.search_in_package);
        doSearchInPackage.setSummary(R.string.also_use_the_package_name_for_searching);
        doSearchInPackage.setDefaultValue(false);

        CheckBoxPreference marketForAllApps = new CheckBoxPreference(this);
        marketForAllApps.setKey(FASTSettings.KEY_MARKETFORALL);
        marketForAllApps.setTitle("" + String.format(getResources().getString(R.string.open_in_for_all), TargetStore.STORE_NAME));
        marketForAllApps.setSummary(R.string.even_if_installed_another_way);
        marketForAllApps.setDefaultValue(false);

        CheckBoxPreference textOnly = new CheckBoxPreference(this);
        textOnly.setKey(FASTSettings.KEY_TEXTONLY);
        textOnly.setTitle(R.string.text_only);
        textOnly.setSummary(R.string.show_no_icons_pure_text);
        textOnly.setDefaultValue(false);

        CheckBoxPreference finishAfterLaunch = new CheckBoxPreference(this);
        finishAfterLaunch.setKey(FASTSettings.KEY_FINISH_ON_LAUNCH);
        finishAfterLaunch.setTitle("Finish on Launch");
        finishAfterLaunch.setTitle("Finish FAST on App-Launch");
        finishAfterLaunch.setDefaultValue(false);

        ListPreference maxLinesPref = new ListPreference(this);
        maxLinesPref.setKey(FASTSettings.KEY_MAXLINES);
        maxLinesPref.setTitle(R.string.max_text_lines);
        maxLinesPref.setSummary(R.string.how_much_text_you_want);
        maxLinesPref.setEntries(new CharSequence[]{"1", "2", "3"});
        maxLinesPref.setEntryValues(new CharSequence[]{"1", "2", "3"});
        maxLinesPref.setDefaultValue("1");

        ListPreference iconSizePref = new ListPreference(this);
        iconSizePref.setKey(FASTSettings.KEY_ICONSIZE);
        iconSizePref.setTitle(R.string.icon_size);
        iconSizePref.setSummary(R.string.how_big_icons);
        iconSizePref.setEntries(R.array.sizes);
        iconSizePref.setEntryValues(new CharSequence[]{"tiny", "small", "medium", "large"});
        iconSizePref.setDefaultValue("medium");

        ListPreference sortPref = new ListPreference(this);
        sortPref.setKey(FASTSettings.KEY_SORT);
        sortPref.setTitle(getString(R.string.sort));
        sortPref.setSummary(getString(R.string.sort_decr));
        sortPref.setEntries(R.array.sort_orders);
        sortPref.setEntryValues(new CharSequence[]{"unsorted", "alpha", "most_used"});
        sortPref.setDefaultValue("unsorted");

        ListPreference themePref = new ListPreference(this);
        themePref.setKey(FASTSettings.KEY_THEME);
        themePref.setTitle(R.string.theme);
        themePref.setSummary(R.string.choose_your_look);
        themePref.setEntries(R.array.themes);
        themePref.setEntryValues(new CharSequence[]{"dark", "light", "transparent", "transparent_light"});
        themePref.setDefaultValue("dark");
        themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                finish();
                startActivity(getIntent());
                return true;
            }
        });

        CheckBoxPreference convertUmlauts = new CheckBoxPreference(this);
        convertUmlauts.setKey(FASTSettings.KEY_UMLAUTCONVERT);
        convertUmlauts.setTitle(R.string.convert_umlauts);
        convertUmlauts.setSummary(R.string.convert_umlauts_descr);
        convertUmlauts.setDefaultValue(false);

        CheckBoxPreference allowGapSearch = new CheckBoxPreference(this);
        allowGapSearch.setKey(FASTSettings.KEY_GAP_SEARCH);
        allowGapSearch.setTitle(R.string.allow_gap_search);
        allowGapSearch.setSummary(R.string.allow_gap_search_descr);
        allowGapSearch.setDefaultValue(true);

        CheckBoxPreference ignoreSpace = new CheckBoxPreference(this);
        ignoreSpace.setKey(FASTSettings.KEY_IGNORESPACEAFTERQUERY);
        ignoreSpace.setTitle(R.string.ignore_space);
        ignoreSpace.setSummary(R.string.ignore_space_descr);
        ignoreSpace.setDefaultValue(false);

        CheckBoxPreference autoShowKeyboard = new CheckBoxPreference(this);
        autoShowKeyboard.setKey(FASTSettings.KEY_SHOWKEYBOARDONSTART);
        autoShowKeyboard.setTitle(R.string.show_keyboard);
        autoShowKeyboard.setSummary(R.string.show_keyboard_descr);
        autoShowKeyboard.setDefaultValue(true);

        PreferenceScreen removeCachePreference = getPreferenceManager().createPreferenceScreen(this);
        removeCachePreference.setTitle(R.string.remove_cache);
        removeCachePreference.setSummary(R.string.remove_cache_descr);
        removeCachePreference.setOnPreferenceClickListener(new CacheRemovingOnPreferenceClickListener());

        root.addPreference(themePref);
        root.addPreference(iconSizePref);
        root.addPreference(maxLinesPref);
        root.addPreference(doLaunchSingleCheckBox);
        root.addPreference(doSearchInPackage);
        root.addPreference(finishAfterLaunch);
        root.addPreference(marketForAllApps);
        root.addPreference(textOnly);
        root.addPreference(sortPref);
        root.addPreference(ignoreSpace);
        root.addPreference(autoShowKeyboard);
        root.addPreference(convertUmlauts);
        root.addPreference(allowGapSearch);
        root.addPreference(removeCachePreference);

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

    public void helpClicked(View v) {
        HelpDialog.show(this);
    }

    private class CacheRemovingOnPreferenceClickListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            new FileHelper(getBaseContext().getCacheDir()).deleteRecursive();
            Intent intent = new Intent(FASTSettingsActivity.this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return false;
        }
    }
}
