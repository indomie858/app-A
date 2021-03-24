package com.example.appa.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.*
import com.example.appa.R
import com.example.appa.ui.MainActivity

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
            val themePref = PreferenceManager.getDefaultSharedPreferences(context)
            val themeId = themePref.getString("theme", "")
            if(id != themeId){
                val contrastPref = PreferenceManager.getDefaultSharedPreferences(context)
                if(id.equals("1")) { // light theme
                    if (contrastPref.getBoolean("contrast", false) == false) {
                        themePref.edit().putString("style", "AppTheme_Light").apply()
                    } else {
                        themePref.edit().putString("style", "App_Theme_Light_HC").apply()
                    }
                }
                else if(id.equals("2")) { // Dark theme
                    if (contrastPref.getBoolean("contrast", false) == false) {
                        themePref.edit().putString("style", "AppTheme_Dark").apply()
                    } else {
                        themePref.edit().putString("style", "AppTheme_Dark_HC").apply()
                    }
                }
            }
            reload()
            true
        }

        val contrast = findPreference<SwitchPreference>("contrast") as SwitchPreference
        contrast.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{preference, newValue ->
            val contrast = newValue as Boolean
            val contrastPref = PreferenceManager.getDefaultSharedPreferences(context)
            val constastId = contrastPref.getBoolean("contrast", false)
            if(contrast != constastId){
                if(contrast == false){ // not HC
                    if(contrastPref.getString("theme", "").equals("1")){
                        contrastPref.edit().putString("style", "AppTheme_Light").apply()
                    } else {
                        contrastPref.edit().putString("style", "AppTheme_Dark").apply()
                    }
                }
                else { // HC
                    if(contrastPref.getString("theme", "").equals("1")) {
                        contrastPref.edit().putString("style", "AppTheme_Light_HC").apply()
                    } else {
                        contrastPref.edit().putString("style", "AppTheme_Dark_HC").apply()
                    }
                }
                contrastPref.edit().putBoolean("contrast", contrast).apply()
                Log.d(tag, "This is for debug: " + contrast)
                val style = contrastPref.getString("style", "")
                Log.d(tag,"this is style: " + style)
            }
            startActivity(Intent(context, MainActivity::class.java))
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

