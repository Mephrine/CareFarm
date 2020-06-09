package kr.smart.carefarm.api

import io.reactivex.Flowable
import kr.smart.carefarm.data.CodeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    // 파일 업로드
    @Multipart
    @POST("insertAttachFileId.json")
    fun sendImage(
        @Part file_1: MultipartBody.Part?,
        @Part("growfacId") growfacId: RequestBody): Flowable<CodeResponse>
}