package kr.smart.carefarm.model

import com.google.gson.annotations.SerializedName

data class WaterData(
    @SerializedName("startTime") val startTime: String,
    @SerializedName("duration") val duration: String)