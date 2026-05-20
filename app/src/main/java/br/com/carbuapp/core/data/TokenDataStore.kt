package br.com.carbuapp.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "carbuapp_prefs")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY      = stringPreferencesKey("jwt_token")
        private val OFICINA_ID_KEY = intPreferencesKey("selected_oficina_id")
    }

    // ── Token JWT ──────────────────────────────────────────────────────────────

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getToken(): String? = tokenFlow.firstOrNull()

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }

    // ── Oficina selecionada (superadmin) ───────────────────────────────────────

    val selectedOficinaIdFlow: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[OFICINA_ID_KEY] }

    suspend fun saveSelectedOficinaId(id: Int) {
        context.dataStore.edit { it[OFICINA_ID_KEY] = id }
    }

    suspend fun getSelectedOficinaId(): Int? = selectedOficinaIdFlow.firstOrNull()

    suspend fun clearSelectedOficinaId() {
        context.dataStore.edit { it.remove(OFICINA_ID_KEY) }
    }

    // ── Limpa tudo (logout) ────────────────────────────────────────────────────

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
