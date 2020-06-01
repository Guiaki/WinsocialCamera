package com.android.example.cameraxbasic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.example.cameraxbasic.di.AppComponent
import com.android.example.cameraxbasic.ui.main.MainActivity
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : AppCompatActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR
            )
            androidContext(this@App)
            modules(AppComponent.components)
        }
        startActivity(Intent(this, MainActivity::class.java))
    }
}
