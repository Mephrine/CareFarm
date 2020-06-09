package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SnsrCriticalData(
    @SerializedName("criId") val criId: String,
    @SerializedName("snsrNm") val snsrNm: String,
    @SerializedName("farmsnsrtagId") val farmsnsrtagId: String,
    @SerializedName("tmzId") val tmzId: String,
    @SerializedName("maxWrnVal") var maxWrnVal: String,
    @SerializedName("minWrnVal") var minWrnVal: String,
//    @SerializedName("maxCauVal") val maxCauVal: String,
//    @SerializedName("minCauVal") val minCauVal: String,
//    @SerializedName("maxDngVal") val maxDngVal: String,
//    @SerializedName("minDngVal") val minDngVal: String,
    @SerializedName("criVal") val criVal: String): Parcelable