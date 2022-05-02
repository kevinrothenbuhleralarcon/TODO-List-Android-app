package ch.kra.todo.core

sealed class Resource<T> {
    data class Success<T>(val data: T): Resource<T>()
    data class Error<T>(val data: T? = null, val message: String): Resource<T>()
    data class Loading<T>(val data: T? = null): Resource<T>()
}
