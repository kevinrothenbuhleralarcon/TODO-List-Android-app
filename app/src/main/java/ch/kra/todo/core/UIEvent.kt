package ch.kra.todo.core

import android.content.Intent

sealed class UIEvent {
    data class ShowSnackbar(val message: String): UIEvent()
    object PopBackStack: UIEvent()
    data class Navigate(val route: String): UIEvent()
    data class DisplayError(val error: String): UIEvent()
    data class DisplaySuccess(val message: String): UIEvent()
    object DisplayLoading: UIEvent()
    data class StartIntent(val intent: Intent): UIEvent()
}
