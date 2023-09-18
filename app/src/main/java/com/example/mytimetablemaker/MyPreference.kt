package com.example.mytimetablemaker

import android.content.Context
import androidx.preference.PreferenceManager

class MyPreference (
    val context: Context,
){

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun prefSaveText(key: String, text: String) {
        prefs.edit().apply {
            putString("dummy", "dummy").apply()
            putString(key, text).apply()
        }
    }

    fun prefSaveBoolean(key: String, isChecked: Boolean) {
        prefs.edit().apply {
            putBoolean("dummy", true).apply()
            putBoolean(key, isChecked).apply()
        }
    }

    fun prefGetBoolean(key: String, defaultValue: Boolean): Boolean =
        PreferenceManager
            .getDefaultSharedPreferences(Application.context)
            .getBoolean(key, defaultValue)
}