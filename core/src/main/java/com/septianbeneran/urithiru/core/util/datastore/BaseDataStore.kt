package com.septianbeneran.urithiru.core.util.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "base_data_store")

class BaseDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @PublishedApi internal val json: Json
) {
    suspend fun saveString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun saveInt(key: String, value: Int) {
        val prefKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun saveBoolean(key: String, value: Boolean) {
        val prefKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun saveLong(key: String, value: Long) {
        val prefKey = longPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun saveFloat(key: String, value: Float) {
        val prefKey = floatPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend inline fun <reified T> saveObject(key: String, value: T) {
        val jsonString = json.encodeToString(value)
        saveString(key, jsonString)
    }

    fun readString(key: String): Flow<String?> {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    fun readInt(key: String): Flow<Int?> {
        val prefKey = intPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    fun readBoolean(key: String): Flow<Boolean?> {
        val prefKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    fun readLong(key: String): Flow<Long?> {
        val prefKey = longPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    fun readFloat(key: String): Flow<Float?> {
        val prefKey = floatPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    inline fun <reified T> readObject(key: String): Flow<T?> {
        return readString(key).map { jsonString ->
            if (jsonString == null) {
                null
            } else {
                try {
                    json.decodeFromString<T>(jsonString)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}