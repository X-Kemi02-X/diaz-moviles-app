package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.local.TokenDataStore
import com.diazmoviles.app.data.remote.api.AuthApi
import com.diazmoviles.app.data.remote.api.LoginRequest
import com.diazmoviles.app.domain.model.AuthTokens
import com.diazmoviles.app.domain.model.LoggedUser
import com.diazmoviles.app.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Pair<AuthTokens, LoggedUser>> {
        return runCatching {
            val response = authApi.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                val tokens = AuthTokens(body.access, body.refresh)
                val user = LoggedUser(body.userId, body.username, body.email, body.isStaff)

                tokenDataStore.saveTokens(body.access, body.refresh)
                tokenDataStore.saveUser(body.userId, body.username, body.email, body.isStaff)

                Pair(tokens, user)
            } else {
                val error = when (response.code()) {
                    401 -> "Credenciales inválidas"
                    else -> "Error del servidor: ${response.code()}"
                }
                throw Exception(error)
            }
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearSession()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenDataStore.getAccessToken() != null
    }

    override suspend fun getLoggedUser(): LoggedUser? {
        val userId = tokenDataStore.getUserId() ?: return null
        val username = tokenDataStore.getUsername() ?: return null
        val email = tokenDataStore.getEmail() ?: return null
        val isStaff = tokenDataStore.getIsStaff()
        return LoggedUser(userId, username, email, isStaff)
    }
}
