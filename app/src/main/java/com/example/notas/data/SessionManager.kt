package com.example.notas.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sessions")

class SessionManager(private val context: Context) {
    companion object {
        private val USER_ID_KEY = longPreferencesKey("user_id")
    }

    val userId: Flow<Long?> = context.dataStore.data.map { preferences ->
        val id = preferences[USER_ID_KEY]
        if (id == -1L || id == null) null else id
    }

    suspend fun saveSession(userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = -1L
        }
    }
}
