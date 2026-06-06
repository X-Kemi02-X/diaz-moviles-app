package com.diazmoviles.app.data.remote.api

import com.diazmoviles.app.data.remote.dto.AuthResponseDto
import com.diazmoviles.app.data.remote.dto.RefreshResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("token/")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponseDto>

    @POST("token/refresh/")
    suspend fun refresh(@Body request: RefreshRequest): Response<RefreshResponseDto>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RefreshRequest(
    val refresh: String
)
