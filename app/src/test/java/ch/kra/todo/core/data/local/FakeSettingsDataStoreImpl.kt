package ch.kra.todo.core.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSettingsDataStoreImpl() : SettingsDataStore {

    private lateinit var token: String
    private lateinit var connectedUser: String

    override val preferenceFlow: Flow<ConnectionPreferences> = flow {
        emit(ConnectionPreferences(token, connectedUser))
    }

    override suspend fun saveTokenToPreferenceStore(token: String) {
        this.token = token
    }

    override suspend fun saveConnectedUserToPreferenceStore(connectedUser: String) {
        this.connectedUser = connectedUser
    }
}