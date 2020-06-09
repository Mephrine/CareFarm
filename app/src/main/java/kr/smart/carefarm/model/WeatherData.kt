package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class WeatherData(
    @SerializedName("iconNo") val iconNo: String,
    @SerializedName("temp") val temp: String,
    @SerializedName("weaText") val weaText: String,
    @SerializedName("rainProb") val rainProb: String,
    @SerializedName("windDir") val windDir: String,
    @SerializedName("windSpeed") val windSpeed: String,
    @SerializedName("humidity") val humidity: String): Parcelable