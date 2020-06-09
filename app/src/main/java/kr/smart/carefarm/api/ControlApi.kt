package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.CodeResponse
import kr.smart.carefarm.data.ControlResponse
import retrofit2.http.GET
import retrofit2.http.Query



interface ControlApi {
    @GET("setFunc.json")
    fun requestControl(@Query("farmequiptagId") farmequiptagId: String,
                       @Query("statusCd") statusCd: String): Flowable<ControlResponse>

    @GET("getFunc.json")
    fun chkControlChangeState(@Query("farmequiptagId") farmequiptagId: String): Flowable<ControlResponse>

    @GET("getStatus.json")
    fun getControlState(@Query("farmequiptagId") farmequiptagId: String): Flowable<ControlResponse>

    @GET("updateStatus.json")
    fun getUpdateState(@Query("farmequiptagId") farmequiptagId: String,
                       @Query("retain") retain: String): Flowable<CodeResponse>



}