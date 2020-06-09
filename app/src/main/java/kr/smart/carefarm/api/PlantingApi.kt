package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.PlantingListResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface PlantingApi {
    @GET("farmDetailList.json")
    fun requestFarmDetail(@Query("farmId") farmId: String): Flowable<PlantingListResponse>
}
