package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jyotish_prefs")

class LanguagePreferences(private val context: Context) {
    private val languageKey = stringPreferencesKey("app_language")

    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        when (prefs[languageKey]) {
            AppLanguage.EN.name -> AppLanguage.EN
            else -> AppLanguage.HI
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[languageKey] = language.name
        }
    }
}
