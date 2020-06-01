package com.android.example.cameraxbasic.data.model

import com.google.gson.annotations.SerializedName

data class DetectedFace(
        @SerializedName("BoundingBox") val boundingBox: BoundingBox,
        @SerializedName("Gender") val gender: Gender,
        @SerializedName("Age") val age: Age
)

data class Age (
        @SerializedName("Age-Range")
        val ageRange: AgeRange
)

data class AgeRange (
        @SerializedName("Low")
        val low: Long,

        @SerializedName("High")
        val high: Long
)

data class BoundingBox (
        val startX: Long,
        val startY: Long,
        val endX: Long,
        val endY: Long,

        @SerializedName("Probability")
        val probability: Double
)

data class Gender (
        @SerializedName("Gender")
        val gender: String,

        @SerializedName("Probability")
        val probability: Double
)