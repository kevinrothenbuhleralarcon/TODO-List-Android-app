package ch.kra.todo.auth.domain.use_case

import android.util.Patterns
import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult

class ValidateEmail {
    operator fun invoke(email: String): ValidationResult {
        if(email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UIText.StringResource(R.string.email_empty_error)
            )
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = UIText.StringResource(R.string.email_validity_error)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}