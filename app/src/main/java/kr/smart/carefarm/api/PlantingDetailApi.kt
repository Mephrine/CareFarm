package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.CodeResponse
import kr.smart.carefarm.data.PlantingListResponse
import kr.smart.carefarm.data.SnsrCriticalListResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query




interface PlantingDetailApi {
    @GET("farmSnsrCriticalList.json")
    fun requestSensorCriticalList(@Query("growfacId") growfacId: String): Flowable<SnsrCriticalListResponse>

    @GET("/farmSnsrCriticalProc.json")
    fun requestModSensorCritical(@Query("criId") criId: String,
                                 @Query("maxWrnVal") maxWrnVal: String,
                                 @Query("minWrnVal") minWrnVal: String,
                                 @Query("loginId") loginId: String): Flowable<CodeResponse>

    @GET("/farmDetail.json")
    fun requestFarmDetail(@Query("farmId") farmId: String,
                                 @Query("growfacId") growfacId: String): Flowable<PlantingListResponse>



}

