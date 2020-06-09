package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TempData(
    @SerializedName("temp") val temp: String,
    @SerializedName("tagGbnList") val tagGbnList: List<String>,
    @SerializedName("tagGbn") val tagGbn: String,
    @SerializedName("warnMax") val warnMax: String,
    @SerializedName("warnMin") val warnMin: String,
    @SerializedName("cautionMax") val cautionMax: String,
    @SerializedName("cautionMin") val cautionMin: String,
    @SerializedName("dangerMax") val dangerMax: String,
    @SerializedName("dangerMin") val dangerMin: String,
    @SerializedName("critical") val critical: String
): Parcelable