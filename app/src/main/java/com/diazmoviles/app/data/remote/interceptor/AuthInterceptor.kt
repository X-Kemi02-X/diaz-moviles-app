package com.diazmoviles.app.data.remote.interceptor

import com.diazmoviles.app.BuildConfig
import com.diazmoviles.app.data.local.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.Semaphore
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class BearerTokenInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val clientProvider: Provider<OkHttpClient>
) : Interceptor {

    private val refreshLock = Semaphore(1)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenDataStore.getAccessToken() }
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        val response = chain.proceed(request)

        if (response.code == 401 && !request.url.toString().contains("token/")) {
            response.close()
            refreshLock.acquire()
            try {
                val refreshToken = runBlocking { tokenDataStore.getRefreshToken() }
                if (refreshToken != null) {
                    val newToken = refreshAccessToken(refreshToken)
                    if (newToken != null) {
                        runBlocking { tokenDataStore.saveAccessToken(newToken) }
                        val retryRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $newToken")
                            .build()
                        return chain.proceed(retryRequest)
                    }
                }
            } finally {
                refreshLock.release()
            }
        }

        return response
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        return try {
            val json = "{\"refresh\":\"$refreshToken\"}"
            val body = json.toRequestBody("application/json".toMediaType())
            val request = okhttp3.Request.Builder()
                .url("${BuildConfig.API_BASE_URL}token/refresh/")
                .post(body)
                .build()
            val response = clientProvider.get().newCall(request).execute()
            if (response.isSuccessful) {
                val jsonObj = JSONObject(response.body?.string() ?: return null)
                jsonObj.getString("access")
            } else null
        } catch (_: Exception) { null }
    }
}
