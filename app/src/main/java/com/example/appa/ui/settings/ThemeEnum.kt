package com.example.appa.ui.settings

import androidx.appcompat.app.AppCompatDelegate

enum class ThemeEnum(val id: String, val mode: Int) {
    MODE_NIGHT_FOLLOW_SYSTEM("0", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    MODE_NIGHT_NO("1", AppCompatDelegate.MODE_NIGHT_NO),
    MODE_NIGHT_YES("2", AppCompatDelegate.MODE_NIGHT_YES);

    companion object {
        fun idOf(id: String) =
                ThemeEnum.values().first { it.id == id }
    }
}