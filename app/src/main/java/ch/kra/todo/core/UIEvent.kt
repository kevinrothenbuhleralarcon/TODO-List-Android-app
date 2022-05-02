package ch.kra.todo.core

import android.content.Intent

sealed class UIEvent {
    data class ShowSnackbar(val message: UIText): UIEvent()
    object PopBackStack: UIEvent()
    data class Navigate(val route: String): UIEvent()
    data class StartIntent(val intent: Intent): UIEvent()
}
