package com.jcarrasco96.une2021.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object Preferences {

    private const val PREF_READ = "pref_read"
    private const val PREF_NIGHT_MODE = "pref_night_mode"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var read: Long
        get() {
            return preferences.getLong(PREF_READ, 0)
        }
        set(read) {
            preferences.edit {
                it.putLong(PREF_READ, read)
            }
        }

    var nightMode: Boolean
        get() {
            return preferences.getBoolean(PREF_NIGHT_MODE, false)
        }
        set(nightMode) {
            preferences.edit {
                it.putBoolean(PREF_NIGHT_MODE, nightMode)
            }
        }

}