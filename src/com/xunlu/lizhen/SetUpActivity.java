package com.xunlu.lizhen;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SetUpActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting);
		Preference pf = findPreference("about");
		EditTextPreference editPf = (EditTextPreference)findPreference("print");
		try {
			pf.setSummary(pf.getSummary() + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
