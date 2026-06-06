package com.diazmoviles.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_ACCESS = stringPreferencesKey("access_token")
        private val KEY_REFRESH = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_IS_STAFF = booleanPreferencesKey("is_staff")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCESS] != null
    }

    val isStaff: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_STAFF] ?: false
    }

    val userId: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    val username: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USERNAME]
    }

    val email: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMAIL]
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCESS]
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_REFRESH]
    }

    suspend fun getAccessToken(): String? = context.dataStore.data.first()[KEY_ACCESS]
    suspend fun getRefreshToken(): String? = context.dataStore.data.first()[KEY_REFRESH]
    suspend fun getUserId(): Int? = context.dataStore.data.first()[KEY_USER_ID]
    suspend fun getUsername(): String? = context.dataStore.data.first()[KEY_USERNAME]
    suspend fun getEmail(): String? = context.dataStore.data.first()[KEY_EMAIL]
    suspend fun getIsStaff(): Boolean = context.dataStore.data.first()[KEY_IS_STAFF] ?: false

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS] = access
            prefs[KEY_REFRESH] = refresh
        }
    }

    suspend fun saveAccessToken(access: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS] = access
        }
    }

    suspend fun saveUser(userId: Int, username: String, email: String, isStaff: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_EMAIL] = email
            prefs[KEY_IS_STAFF] = isStaff
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
