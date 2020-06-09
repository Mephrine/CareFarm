package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CropDetailData(
    @SerializedName("cropId") val cropId: String,
    @SerializedName("cropNm") val cropNm: String,
    @SerializedName("cropDivn") val cropDivn: String,
    @SerializedName("cropDivnNm") val cropDivnNm: String,
    @SerializedName("useYn") val useYn: String,
    @SerializedName("termYn") val termYn: String,
    @SerializedName("envList") val envList: String,
    @SerializedName("noteTxt") val noteTxt: String,
    @SerializedName("farmId") val farmId: String,
    @SerializedName("growfacId") val growfacId: String,
    @SerializedName("regDate") val regDate: String,
    @SerializedName("regId") val regId: String,
    @SerializedName("regNm") val regNm: String,
    @SerializedName("modDate") val modDate: String,
    @SerializedName("modId") val modId: String,
    @SerializedName("cropcylId") val cropcylId: String,
    @SerializedName("ordSeq") val ordSeq: String,
    @SerializedName("cylNm") val cylNm: String,
    @SerializedName("cylTerm") val cylTerm: String
) : Parcelable