package com.example.appa.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.appa.R

class SettingsFragment : PreferenceFragmentCompat() {


    // private val preferences: SharedPreferences = by inject()
    // activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    //private val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)




/*
    override fun onResume() {
        super.onResume()
        //preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        //preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        PreferenceManager.getDefaultSharedPreferences(context)
                .unregisterOnSharedPreferenceChangeListener(this)
    }
    */

    companion object{
        fun newInstance(rootkey: String):SettingsFragment{
            var settingsFragment = SettingsFragment()
            var bundle = Bundle()
            bundle.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, rootkey)
            settingsFragment.arguments = bundle
            return SettingsFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Theme preference
        val theme = findPreference<ListPreference>("theme") as ListPreference
        theme.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{preference, newValue ->
            val id = newValue as String
            ThemeSetting.setDefaultNightMode(ThemeEnum.idOf(id))
            reload()
            true
        }

        // Distance unit
        val dunit = findPreference<ListPreference>("dunit") as ListPreference
        dunit.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{preference, newValue ->
            val dunit = newValue as String
            val dunitPref = PreferenceManager.getDefaultSharedPreferences(context)
            val dunitId = dunitPref.getString("dunit", "")
            if(dunit != dunitId){
                dunitPref.edit().putString("dunit", dunit).apply()
            }

            true
        }
    }

    private fun reload() {
        getParentFragmentManager().beginTransaction().remove(this).commit()
    }


/*
    private fun theme() {
        findPreference<ListPreference>("theme")?.apply {
            setOnPreferenceChangeListener{ preference, newValue ->
                val id = newValue as String
                ThemeSetting.setDefaultNightMode(ThemeEnum.idOf(id))
                true
            }
        }

    }*/



}

