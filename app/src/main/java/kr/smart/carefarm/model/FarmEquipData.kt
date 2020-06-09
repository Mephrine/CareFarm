package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FarmEquipData(@SerializedName("farmequiptagId") val farmequiptagId: String,
                    @SerializedName("growfacId") val growfacId: String,
                    @SerializedName("farmequipId") val farmequipId: String,
                    @SerializedName("growfacNm") val growfacNm: String,
                    @SerializedName("farmId") val farmId: String,
                    @SerializedName("tagNm") val tagNm: String,
                    @SerializedName("tagId") val tagId: String,
                    @SerializedName("tagStatusId") val tagStatusId: String,
                    @SerializedName("tagDivn") val tagDivn: String,
                    @SerializedName("tagDtlDivn") val tagDtlDivn: String,
                    @SerializedName("tagDtlDivnNm") val tagDtlDivnNm: String,
                    @SerializedName("calTag") val calTag: String,
                    @SerializedName("useYn") val useYn: String,
                    @SerializedName("delYn") val delYn: String,
                    @SerializedName("noteTxt") val noteTxt: String,
                    @SerializedName("regDate") val regDate: String,
                    @SerializedName("regId") val regId: String,
                    @SerializedName("modDate") val modDate: String,
                    @SerializedName("modId") val modId: String,
                    @SerializedName("colctIp") val colctIp: String,
                    @SerializedName("colctPort") val colctPort: String,
                    @SerializedName("equipNm") val equipNm: String,
                    @SerializedName("retVal") val retVal: String,
                    @SerializedName("retStatus") val retStatus: String): Parcelable