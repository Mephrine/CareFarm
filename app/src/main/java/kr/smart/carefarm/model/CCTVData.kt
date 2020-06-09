package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CCTVData(@SerializedName("farmcctvId") val farmcctvId: String,
                    @SerializedName("farmId") val farmId: String,
                    @SerializedName("corpId") val corpId: String,
                    @SerializedName("growfacId") val growfacId: String,
                    @SerializedName("cctvNm") val cctvNm: String,
                    @SerializedName("cctvUrl") val cctvUrl: String,
                    @SerializedName("wsUrl") val wsUrl: String,
                    @SerializedName("cctvId") val cctvId: String,
                    @SerializedName("cctvPw") val cctvPw: String,
                    @SerializedName("cctvIp") val cctvIp: String,
                    @SerializedName("cctvWebport") val cctvWebport: String,
                    @SerializedName("cctvVideoport") val cctvVideoport: String,
                    @SerializedName("cctvChannel") val cctvChannel: String,
                    @SerializedName("instlDate") val instlDate: String,
                    @SerializedName("instlMny") val instlMny: String,
                    @SerializedName("grtsEnddate") val grtsEnddate: String,
                    @SerializedName("mngId") val mngId: String,
                    @SerializedName("useYn") val useYn: String,
                    @SerializedName("delYn") val delYn: String,
                    @SerializedName("noteTxt") val noteTxt: String,
                    @SerializedName("regDate") val regDate: String,
                    @SerializedName("regId") val regId: String,
                    @SerializedName("modDate") val modDate: String,
                    @SerializedName("modId") val modId: String,
                    @SerializedName("siteDivn") val siteDivn: String): Parcelable