package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.CodeResponse
import kr.smart.carefarm.data.CropDetailResponse
import kr.smart.carefarm.data.CropResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CropApi {
    @GET("getCropList.json")
    fun requestCropList(@Query("farmId") farmId: String): Flowable<CropResponse>

    @GET("getCropDetail.json")
    fun requestCropDetail(@Query("cropId") cropId: String): Flowable<CropDetailResponse>

    // 작물정보
    @GET("insertSetCrop.json")
    fun requestInsertSetCrop(@Query("cropId") cropId: String,
                             @Query("cropNm") cropNm: String,
                             @Query("cropDivn") cropDivn: String,
                             @Query("useYn") useYn: String,
                             @Query("termYn") termYn: String,
                             @Query("envList") envList: String,
                             @Query("noteTxt") noteTxt: String,
                             @Query("farmIdfarmId") farmIdfarmId: String,
                             @Query("growfacId") growfacId: String,
                             @Query("loginId") loginId: String): Flowable<CodeResponse>

    @GET("updateSetCrop.json")
    fun requestUpdateSetCrop(@Query("cropId") cropId: String,
                             @Query("cropNm") cropNm: String,
                             @Query("cropDivn") cropDivn: String,
                             @Query("useYn") useYn: String,
                             @Query("termYn") termYn: String,
                             @Query("envList") envList: String,
                             @Query("noteTxt") noteTxt: String,
                             @Query("farmIdfarmId") farmIdfarmId: String,
                             @Query("growfacId") growfacId: String,
                             @Query("loginId") loginId: String): Flowable<CodeResponse>

    @GET("deleteSetCrop.json")
    fun requestDeleteSetCrop(@Query("cropId") cropId: String): Flowable<CodeResponse>


    // 작물 주기
    @GET("insertSetCropcyl.json")
    fun requestInsertSetCropcyl(@Query("cropId") cropId: String,
                                @Query("ordSeq") ordSeq: String,
                                @Query("cylNm") cylNm: String,
                                @Query("cylTerm") cylTerm: String,
                                @Query("useYn") useYn: String,
                                @Query("noteTxt") noteTxt: String,
                                @Query("loginId") loginId: String): Flowable<CodeResponse>

    @GET("updateSetCropcyl.json")
    fun requestUpdateSetCropcyl(@Query("cropId") cropId: String,
                                @Query("cropcylId") cropcylId: String,
                                @Query("ordSeq") ordSeq: String,
                                @Query("cylNm") cylNm: String,
                                @Query("cylTerm") cylTerm: String,
                                @Query("useYn") useYn: String,
                                @Query("noteTxt") noteTxt: String,
                                @Query("loginId") loginId: String): Flowable<CodeResponse>

    @GET("deleteSetCropcyl.json")
    fun requestDeleteSetCropcyl(@Query("cropId") cropId: String): Flowable<CodeResponse>

}