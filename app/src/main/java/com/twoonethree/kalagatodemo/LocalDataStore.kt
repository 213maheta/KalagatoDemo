package com.twoonethree.kalagatodemo

import android.content.Context
import android.content.SharedPreferences


class LocalDataStore(context: Context) {

    companion object {
        const val PREFS_NAME = "app_prefs"
        const val IS_DARK_THEME = "is_dark_theme"
        const val PRIMARY_COLOR = "primary_color"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun <T> save(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported data type")
            }
            apply()
        }
    }

    // Generic function to get data with default value
    fun <T> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported data type")
        }
    }

}
