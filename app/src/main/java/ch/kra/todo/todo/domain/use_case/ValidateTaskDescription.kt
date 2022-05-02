package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.core.ValidationResult

class ValidateTaskDescription {

    operator fun invoke(description: String): ValidationResult {
        if (description.isBlank()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = "The description cannot be blank"
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}