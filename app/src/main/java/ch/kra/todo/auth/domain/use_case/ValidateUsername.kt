package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult

class ValidateUsername {
    operator fun invoke(username: String): ValidationResult {
        if (username.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = UIText.StringResource(R.string.username_error)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}