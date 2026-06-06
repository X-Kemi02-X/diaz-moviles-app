package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    val access: String,
    val refresh: String,
    @SerializedName("user_id") val userId: Int,
    val username: String,
    val email: String,
    @SerializedName("is_staff") val isStaff: Boolean
)
