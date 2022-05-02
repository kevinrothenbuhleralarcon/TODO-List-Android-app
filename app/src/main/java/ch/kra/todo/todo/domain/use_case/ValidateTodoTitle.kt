package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.core.ValidationResult

class ValidateTodoTitle {

    operator fun invoke(title: String): ValidationResult {
        if (title.isBlank()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = "The title cannot be blank"
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}