package com.diazmoviles.app.domain.repository

import com.diazmoviles.app.domain.model.AuthTokens
import com.diazmoviles.app.domain.model.LoggedUser

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Pair<AuthTokens, LoggedUser>>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
