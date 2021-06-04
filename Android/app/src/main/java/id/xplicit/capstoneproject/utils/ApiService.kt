package id.xplicit.capstoneproject.utils

import id.xplicit.capstoneproject.entity.RemoteResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("/main-app")
    fun getPredictionResult(
        @Part image: MultipartBody.Part
    ): Call<RemoteResponse>
}