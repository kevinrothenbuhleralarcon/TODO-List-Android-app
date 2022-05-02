package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult

class ValidatePassword {
    operator fun invoke(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = UIText.StringResource(R.string.password_error)
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}