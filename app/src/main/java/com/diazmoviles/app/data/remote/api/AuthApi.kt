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

    @POST("change-password/")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RefreshRequest(
    val refresh: String
)

data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String
)

data class ChangePasswordResponse(
    val detail: String
)
