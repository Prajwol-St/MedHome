package com.example.medhomeapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.medhomeapp.utils.LanguageManager

open class BaseActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val context = LanguageManager.applyLanguage(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
