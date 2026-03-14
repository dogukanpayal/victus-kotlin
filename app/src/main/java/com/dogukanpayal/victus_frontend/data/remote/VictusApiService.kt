package com.dogukanpayal.victus_frontend.data.remote

import com.dogukanpayal.victus_frontend.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface VictusApiService {
    @POST("v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("v1/user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>
}
