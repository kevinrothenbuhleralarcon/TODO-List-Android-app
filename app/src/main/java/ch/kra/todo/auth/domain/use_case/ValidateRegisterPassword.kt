package ch.kra.todo.auth.domain.use_case

import androidx.core.util.PatternsCompat
import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult

class ValidateRegisterPassword {
    operator fun invoke(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = UIText.StringResource(R.string.password_error)
            )
        }
        val passwordPattern = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*!@#%^&(){}:;<>,.?/~_+=|\[\]\\-]).{8,}$""".toRegex()
        if (passwordPattern.find(password) == null) {
            return ValidationResult(
                successful = false,
                errorMessage = UIText.StringResource(R.string.password_pattern_error)
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}