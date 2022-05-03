package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult

class ValidateRepeatedRegisterPassword {
    operator fun invoke(password: String, repeatedPassword: String): ValidationResult {
        if(password != repeatedPassword) {
            return ValidationResult(
                successful = false,
                errorMessage = UIText.StringResource(R.string.password_mismatch_error)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}