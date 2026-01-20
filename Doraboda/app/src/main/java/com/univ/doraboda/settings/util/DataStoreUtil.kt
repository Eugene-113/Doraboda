package com.univ.doraboda.settings.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreUtil @Inject constructor(@ApplicationContext val context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val LABELS_KEY = intPreferencesKey("labels")
    val QUOTE_KEY = booleanPreferencesKey("quote")

    fun getLabelSetting(): Flow<Int> = context.dataStore.data.catch { ex->
        Timber.Forest.d("datastore error: ${ex}")
        emit(emptyPreferences())
    }.map { preferences ->
        preferences[LABELS_KEY] ?: 0
    }

    fun setLabelSetting(label: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences[LABELS_KEY] = label
            }
        }
    }

    fun getQuoteSetting(): Flow<Boolean> = context.dataStore.data.catch { ex->
        Timber.Forest.d("datastore error: ${ex}")
        emit(emptyPreferences())
    }.map { preferences ->
        preferences[QUOTE_KEY] ?: true
    }

    suspend fun setQuoteSetting(label: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[QUOTE_KEY] = label
        }
    }
}