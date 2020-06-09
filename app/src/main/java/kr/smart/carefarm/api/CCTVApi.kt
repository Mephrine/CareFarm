package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.CCTVListResponse
import kr.smart.carefarm.data.LoginResponse
import retrofit2.http.GET
import retrofit2.http.Query



interface CCTVApi {
    @GET("farmCctvList.json")
    fun farmCCTVList(@Query("farmId") farmId: String): Flowable<CCTVListResponse>

    @GET("farmCctvList.json")
    fun growthCCTVList(@Query("growfacId") growfacId: String): Flowable<CCTVListResponse>

    @GET("farmCctvDetail.json")
    fun CCTVDetail(@Query("farmcctvId") farmcctvId: String): Flowable<LoginResponse>

}
