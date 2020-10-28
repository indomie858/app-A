package com.example.appa.ui.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class ThemeSetting {
    companion object {
        fun setDefaultNightMode(themeEnum: ThemeEnum) =
                AppCompatDelegate.setDefaultNightMode(themeEnum.mode)

        fun setDefaultNightModeByPreference(context: Context) {
            val themePreference = PreferenceManager.getDefaultSharedPreferences(context)
            val id = themePreference.getString("theme", ThemeEnum.MODE_NIGHT_FOLLOW_SYSTEM.id)
            setDefaultNightMode(ThemeEnum.idOf(id!!))
        }
    }
}