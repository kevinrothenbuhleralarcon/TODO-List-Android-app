package ch.kra.todo.core

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: UIText? = null
)
