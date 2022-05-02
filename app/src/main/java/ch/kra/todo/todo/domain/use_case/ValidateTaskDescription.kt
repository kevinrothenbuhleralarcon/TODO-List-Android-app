package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult

class ValidateTaskDescription {

    operator fun invoke(description: String): ValidationResult {
        if (description.isBlank()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = UIText.StringResource(R.string.description_error)
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}