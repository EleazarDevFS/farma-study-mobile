package com.example.farmastudy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session"
)
class SessionStore (private val context: Context) {
    private val dataStore = context.sessionDataStore
    private val keyUserName = stringPreferencesKey("logged_username")

    val username: Flow<String?> = dataStore.data.catch {
        exception ->
        if(exception is IOException) emit(emptyPreferences()) else throw  exception
    }.map {  it[keyUserName] }

    suspend fun save(username: String) {
        dataStore.edit { it[keyUserName] = username }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(keyUserName) }
    }
}