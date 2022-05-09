package ch.kra.todo.core.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSettingsDataStoreImpl(private var token: String = "", private var connectedUser: String = "" ) : SettingsDataStore {

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