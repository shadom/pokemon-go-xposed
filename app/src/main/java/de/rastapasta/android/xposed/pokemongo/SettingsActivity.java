package de.rastapasta.android.xposed.pokemongo;


import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import de.rastapasta.android.utils.AppCompatPreferenceActivity;
import de.rastapasta.android.xposed.pokemongo.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager prefMgr = getPreferenceManager();
            prefMgr.setSharedPreferencesName("preferences");
            prefMgr.setSharedPreferencesMode(MODE_WORLD_READABLE);

            addPreferencesFromResource(R.xml.preferences);
            setHasOptionsMenu(false);

            bindPreferenceSummaryToValue(findPreference("endpoint"));

            findPreference("hide_app").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    boolean b = (Boolean) newVal;
                    Context c = preference.getContext();
                    PackageManager pm = c.getPackageManager();
                    pm.setComponentEnabledSetting(
                            new ComponentName(c, SettingsActivity.class.getPackage().getName()+".show_ic"),
                            b ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED: PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                    );
                    return true;
                }
            });
        }
    }

}
