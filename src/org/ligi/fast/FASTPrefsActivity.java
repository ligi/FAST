package org.ligi.fast;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
/**
 * Activity to Edit the Preferences 
 *
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 *
 */
public class FASTPrefsActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(  savedInstanceState );
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		PreferenceScreen prefs_screen=createPreferenceHierarchy();
		setPreferenceScreen(prefs_screen);

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
		marketForAllApps.setTitle(""+ String.format(getResources().getString(R.string.open_in_for_all), ApplicationContext.STORE_NAME));
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
		maxLinesPref.setEntries(new CharSequence[] { "1","2","3"});
		maxLinesPref.setEntryValues(new CharSequence[] { "1","2","3"});
		maxLinesPref.setDefaultValue("1");
				
		root.addPreference(doLaunchSingleCheckBox);
		root.addPreference(doSearchInPackage);
		root.addPreference(marketForAllApps);
		root.addPreference(textOnly);
		root.addPreference(maxLinesPref);
		
		return root;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}
}
