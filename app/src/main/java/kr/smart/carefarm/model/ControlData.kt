package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ControlData(@SerializedName("atchFileId") val atchFileId: String,
                        @SerializedName("atchFileIdNm") val atchFileIdNm: String,
                      @SerializedName("farmequipId") val farmequipId: String,
                      @SerializedName("farmequiptagId") val farmequiptagId: String,
                      @SerializedName("retVal") var retVal: String,
                      @SerializedName("retStatus") var retStatus: String?,
                       @SerializedName("retain") var retain: String,
                       @SerializedName("retainCnt") var retainCnt: String?,
                      @SerializedName("growfacId") val growfacId: String,
                      @SerializedName("growfacNm") val growfacNm: String,
                      @SerializedName("farmId") val farmId: String,
                      @SerializedName("tagNm") val tagNm: String,
                      @SerializedName("tagDivn") val tagDivn: String,
                      @SerializedName("tagDtlDivn") val tagDtlDivn: String,
                      @SerializedName("tagDtlDivnNm") val tagDtlDivnNm: String,
                      @SerializedName("tagId") val tagId: String,
                      @SerializedName("tagStatusId") val tagStatusId: String,
                      @SerializedName("calTag") val calTag: String,
                      @SerializedName("equipNm") val equipNm: String,
                      @SerializedName("useYn") val useYn: String,
                      @SerializedName("delYn") val delYn: String,
                       var isControlling: Boolean?): Parcelable