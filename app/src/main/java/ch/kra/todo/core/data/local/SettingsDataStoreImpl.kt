package ch.kra.todo.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsDataStoreImpl(private val dataStore: DataStore<Preferences>) : SettingsDataStore {
    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
        val CONNECTED_USER = stringPreferencesKey("connected_user")
    }

    override val preferenceFlow: Flow<ConnectionPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val token = preferences[PreferencesKeys.TOKEN] ?: ""
            val connectedUser = preferences[PreferencesKeys.CONNECTED_USER] ?: ""
            ConnectionPreferences(token, connectedUser)
        }

    override suspend fun saveTokenToPreferenceStore(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = token
        }
    }

    override suspend fun saveConnectedUserToPreferenceStore(connectedUser: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONNECTED_USER] = connectedUser
        }
    }
}