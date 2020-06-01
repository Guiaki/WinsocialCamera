package com.android.example.cameraxbasic.data.remote

import com.google.gson.annotations.SerializedName

data class AgeGenderRequestBody(
        @SerializedName("url")
        val url: String,
        @SerializedName("accuracy_boost")
        val accuracyBoost: Int = 3
)