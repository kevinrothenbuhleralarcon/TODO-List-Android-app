package ch.kra.todo.core.data.local

import kotlinx.coroutines.flow.Flow

interface SettingsDataStore {
    val preferenceFlow: Flow<ConnectionPreferences>

    suspend fun saveTokenToPreferenceStore(token: String)

    suspend fun saveConnectedUserToPreferenceStore(connectedUser: String)
}