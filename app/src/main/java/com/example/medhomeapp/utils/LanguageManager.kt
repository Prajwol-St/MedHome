package com.example.medhomeapp.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    const val ENGLISH = "en"
    const val NEPALI = "ne"

    private const val PREF_NAME = "language_pref"
    private const val KEY_LANGUAGE = "selected_language"

    fun setLanguage(context: Context, language: String) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    fun getLanguage(context: Context): String {
        return context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, ENGLISH) ?: ENGLISH
    }

    fun applyLanguage(base: Context): Context {
        val language = getLanguage(base)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)

        return base.createConfigurationContext(config)
    }
}
