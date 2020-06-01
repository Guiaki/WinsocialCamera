package com.android.example.cameraxbasic.di.module

import android.content.Context
import com.android.example.cameraxbasic.BuildConfig
import com.android.example.cameraxbasic.data.service.ApiService
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import org.koin.dsl.module

object NetModule {
    val module = module {
        single { provideCache(get()) }
        single { provideOkHttpClient(get()) }
        single { provideRetrofit(get(), BuildConfig.API_URL) }
        single { provideAppService(get()) }
    }

    private fun provideCache(context: Context): Cache {
        val cacheSize: Long = 10 * 1024 * 1024 // 10mb
        return Cache(context.cacheDir, cacheSize)
    }

    private fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(logger)
                .build()
    }

    private fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun provideAppService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}