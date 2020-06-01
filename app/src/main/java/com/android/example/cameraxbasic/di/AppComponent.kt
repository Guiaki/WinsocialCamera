package com.android.example.cameraxbasic.di

import com.android.example.cameraxbasic.di.module.NetModule
import com.android.example.cameraxbasic.ui.main.MainModule

object AppComponent {
    val components = listOf(
            NetModule.module,
            MainModule.module
    )
}