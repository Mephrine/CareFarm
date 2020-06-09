package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.CodeResponse
import retrofit2.http.POST
import retrofit2.http.Query



interface LogoutApi {
    @POST("access/frontLogout.json")
    fun sendLogout(@Query("userId") userId: String): Flowable<CodeResponse>
}