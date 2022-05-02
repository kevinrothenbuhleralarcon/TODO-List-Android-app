package ch.kra.todo.core

data class ValidationResult(
    val sucessful: Boolean,
    val errorMessage: UIText? = null
)
