package com.example.appa.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.appa.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static Fragment newInstance(String rootKey) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, rootKey);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        switch (rootKey) {
            case "preference_appearance":
                onCreateAppearancePreferences();
                break;
            case "preference_others":
                break;
        }
    }

    private void onCreateAppearancePreferences() {

        final ListPreference themePreference = (ListPreference) findPreference("preference_theme");
        themePreference.setSummary(themePreference.getEntry());
        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int indexOfValue = themePreference.findIndexOfValue(String.valueOf(newValue));
                themePreference.setSummary(indexOfValue >= 0 ? themePreference.getEntries()[indexOfValue] : null);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        String rootKey = getArguments().getString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT);
        getActivity().setTitle(findPreference(rootKey).getTitle());
    }

}
