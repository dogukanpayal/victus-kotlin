package com.dogukanpayal.victus_frontend.data.remote

import com.dogukanpayal.victus_frontend.data.model.RegisterRequest
import com.dogukanpayal.victus_frontend.data.model.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VictusApiService {
    @POST("v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ProfileResponse>
}
