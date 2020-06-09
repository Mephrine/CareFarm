package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.LoginResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {
    @POST("access/cfarmLoginAction.json")
    fun sendLogin(@Query("userId") userId: String,
                  @Query("userPwd") userPwd: String,
                  @Query("token") token: String): Flowable<LoginResponse>
}


