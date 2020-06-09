package kr.smart.carefarm.model

import android.os.Parcelable
import android.view.Window
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kr.smart.carefarm.R
import java.io.Serializable

data class PlantingType (
    val growfacNm: String
) {
    enum class Type { SINGLE, PLURAL }
    var type =  Type.SINGLE
    var typeNmResource: Int = R.string.planting_single

    init {
        when(growfacNm) {
            "단동" -> {
                type = Type.SINGLE
                typeNmResource = R.string.planting_single
            }
            "연동" -> {
                type = Type.PLURAL
                typeNmResource = R.string.planting_plural
            }
//            "M" -> type = Type.MIX
        }

    }
}

@Parcelize
data class PlantingData(@SerializedName("growfacId") val growfacId: String,
                    @SerializedName("farmId") val farmId: String,
                        @SerializedName("growfacNm") val growfacNm: String,
                        @SerializedName("growfacDivn") val growfacDivn: String,
                        @SerializedName("cropId") val cropId: String,
                        @SerializedName("maxLdadngNum") val maxLdadngNum: String,
                        @SerializedName("proprtLdadngNum") val proprtLdadngNum: String,
                        @SerializedName("columnNum") val columnNum: String,
                        @SerializedName("spceNum") val spceNum: String,
                        @SerializedName("stgNum") val stgNum: String,
                        @SerializedName("instlDate") val instlDate: String,
                        @SerializedName("useYn") val useYn: String,
                        @SerializedName("delYn") val delYn: String,
                        @SerializedName("noteTxt") val noteTxt: String,
                        @SerializedName("regDate") val regDate: String,
                        @SerializedName("regId") val regId: String,
                        @SerializedName("modDate") val modDate: String,
                        @SerializedName("modId") val modId: String,
                        @SerializedName("farmequipId") val farmequipId: String,
                        @SerializedName("farmsnsrId") val farmsnsrId: String,
                        @SerializedName("atchFileId") val atchFileId: String,
                        @SerializedName("windowUTagList") val windowUTagList: List<ControlData>?,
                        @SerializedName("windowSTagList") val windowSTagList: List<ControlData>?,
                        @SerializedName("warterTagList") val warterTagList: List<ControlData>?,
                        @SerializedName("snsrTagList") val snsrTagList: List<ControlData>?): Parcelable