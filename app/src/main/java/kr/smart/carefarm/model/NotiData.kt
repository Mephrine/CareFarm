package kr.smart.carefarm.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotiData(@SerializedName("ntcobSeq") val ntcobSeq: String,
                    @SerializedName("ntcobTitle") val ntcobTitle: String,
                    @SerializedName("ntcobCont") val ntcobCont: String,
                    @SerializedName("linkUrl") val linkUrl: String,
                    @SerializedName("siteDivn") val siteDivn: String,
                    @SerializedName("targetDivn") val targetDivn: String,
                    @SerializedName("targetDesc") val targetDesc: String,
                    @SerializedName("sendYn") val sendYn: String,
                    @SerializedName("sendDate") val sendDate: String,
                    @SerializedName("sendTime") val sendTime: String,
                    @SerializedName("delYn") val delYn: String,
                    @SerializedName("atchFileId") val atchFileId: String,
                    @SerializedName("regDate") val regDate: String,
                    @SerializedName("regNm") val regNm: String,
                    @SerializedName("regId") val regId: String): Parcelable