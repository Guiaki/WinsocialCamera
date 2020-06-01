package com.android.example.cameraxbasic.ui.main

import com.android.example.cameraxbasic.data.service.ApiService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object MainModule {
    val module = module {
        viewModel { MainViewModel() }
    }
}