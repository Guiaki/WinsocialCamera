package com.android.example.cameraxbasic.data.service

import com.android.example.cameraxbasic.data.model.DetectedFaces
import com.android.example.cameraxbasic.data.remote.AgeGenderRequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.android.example.cameraxbasic.BuildConfig

interface ApiService {

    @Headers("x-rapidapi-key: ${BuildConfig.API_KEY}")
    @POST("/img/face-age-gender")
    suspend fun requestFace(
            @Body ageGenderRequestBody: AgeGenderRequestBody
    ): DetectedFaces?
}