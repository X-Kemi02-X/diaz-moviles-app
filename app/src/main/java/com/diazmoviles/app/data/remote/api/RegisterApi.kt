package com.diazmoviles.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val nombre: String,
    val apellido: String,
    val cedula: String,
    val telefono: String,
    val direccion: String
)

data class RegisterResponse(
    val user_id: Int,
    val cliente_id: Int,
    val username: String,
    val email: String
)

interface RegisterApi {
    @POST("register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}
