package org.ligi.fast;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class FASTPrefsActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);

		CheckBoxPreference doLegendCheckBoxPref = new CheckBoxPreference(this);
		doLegendCheckBoxPref.setKey(FASTPrefs.KEY_LAUNCHSINGLE);
		doLegendCheckBoxPref.setTitle(R.string.launch_single);
		doLegendCheckBoxPref.setSummary("Auto-Launch if only one App left ");
		doLegendCheckBoxPref.setDefaultValue(false);
		// doLegendCheckBoxPref.setOnPreferenceChangeListener(this);

		root.addPreference(doLegendCheckBoxPref);
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
