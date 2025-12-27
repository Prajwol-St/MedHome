package com.example.medhomeapp

import android.app.Application
import com.example.medhomeapp.utils.LanguageManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //this applies saved language when app starts
        val languageCode = LanguageManager.getLanguage(this)
        LanguageManager.setLanguage(this, languageCode)
    }
}