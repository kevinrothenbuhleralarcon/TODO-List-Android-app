package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult

class ValidateTodoTitle {

    operator fun invoke(title: String): ValidationResult {
        if (title.isBlank()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = UIText.StringResource(R.string.todo_title_error)
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}