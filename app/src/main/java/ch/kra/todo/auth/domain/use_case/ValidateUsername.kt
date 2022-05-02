package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.core.ValidationResult

class ValidateUsername {
    operator fun invoke(username: String): ValidationResult {
        if (username.isEmpty()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = "Username cannot be empty"
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}