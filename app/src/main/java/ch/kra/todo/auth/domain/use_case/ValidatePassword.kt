package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.core.ValidationResult

class ValidatePassword {
    operator fun invoke(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = "Password cannot be empty"
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}