package com.example.medhomeapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.medhomeapp.utils.LanguageManager

open class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Language will be applied automatically
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.applyLanguage(newBase))
    }
}