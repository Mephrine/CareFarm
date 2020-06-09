package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.NotiListResponse
import retrofit2.http.POST
import retrofit2.http.Query


interface NotiApi {
    @POST("cfarmPushList.json")
    fun notiAllList(@Query("userId") userId: String): Flowable<NotiListResponse>

    @POST("cfarmPushList.json")
    fun notiFarmList(@Query("userId") userId: String,
                 @Query("farmId") farmId: String): Flowable<NotiListResponse>

    @POST("updateToken.json")
    fun updateToken(@Query("userId") userId: String,
                     @Query("token") token: String): Flowable<NotiListResponse>

}
