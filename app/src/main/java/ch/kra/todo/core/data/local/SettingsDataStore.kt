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

/*private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CONNECTION_PREFERENCE_NAME
)*/

class SettingsDataStore(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
        val CONNECTED_USER = stringPreferencesKey("connected_user")
    }

    val preferenceFlow: Flow<ConnectionPreferences> = dataStore.data
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

    suspend fun saveTokenToPreferenceStore(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = token
        }
    }

    suspend fun saveConnectedUserToPreferenceStore(connectedUser: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONNECTED_USER] = connectedUser
        }
    }
}