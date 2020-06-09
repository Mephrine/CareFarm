package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.FarmResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FarmApi {
    @GET("selectFarmList.json")
    fun requestFarmList(@Query("userId") userId: String): Flowable<FarmResponse>
}