package com.android.example.cameraxbasic.data.model

import com.google.gson.annotations.SerializedName

data class DetectedFaces(
        @SerializedName("employees") val employees: List<DetectedFace> = listOf()
)