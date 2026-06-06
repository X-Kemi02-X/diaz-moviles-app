package com.diazmoviles.app.domain.model

data class AuthTokens(
    val access: String,
    val refresh: String
)

data class LoggedUser(
    val userId: Int,
    val username: String,
    val email: String,
    val isStaff: Boolean
)
