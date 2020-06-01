package com.android.example.cameraxbasic.data.model

import com.google.gson.annotations.SerializedName

data class DetectedFaces(
        @SerializedName("detected_faces") val detectedFaces: List<DetectedFace> = listOf()
)