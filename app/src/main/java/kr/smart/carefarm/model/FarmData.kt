package kr.smart.carefarm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class FarmData(@SerializedName("farmId") val farmId: String,
                    @SerializedName("farmNm") val farmNm: String,
                    @SerializedName("ceoNm") val ceoNm: String,
                    @SerializedName("areaCd") val areaCd: String,
                    @SerializedName("postNo") val postNo: String,
                    @SerializedName("addrNm1") val addrNm1: String,
                    @SerializedName("addrNm2") val addrNm2: String,
                    @SerializedName("addrNm3") val addrNm3: String,
                    @SerializedName("areaGrow") val areaGrow: String,
                    @SerializedName("areaExProduct") val areaExProduct: String,
                    @SerializedName("areaNewFarm") val areaNewFarm: String,
                    @SerializedName("areaNeedPort") val areaNeedPort: String,
                    @SerializedName("areaType") val areaType: String,
                    @SerializedName("notiList") val notiList: List<NotiData>?): Parcelable