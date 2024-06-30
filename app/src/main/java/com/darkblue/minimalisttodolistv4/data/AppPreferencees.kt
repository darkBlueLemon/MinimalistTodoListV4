package com.darkblue.minimalisttodolistv4.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PREFERENCES_NAME = "settings"

class AppPreferences(context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)
    private val dataStore = context.dataStore

    val THEME_KEY = stringPreferencesKey("theme")
    val theme: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: "Dark"
        }
    suspend fun saveTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}