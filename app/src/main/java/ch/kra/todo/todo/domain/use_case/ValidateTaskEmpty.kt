package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.R
import ch.kra.todo.core.UIText
import ch.kra.todo.core.ValidationResult
import ch.kra.todo.todo.presentation.add_edit_todo.TaskFormState

class ValidateTaskEmpty {
    operator fun invoke(tasks: List<TaskFormState>): ValidationResult {
        if (tasks.isEmpty()) {
            return ValidationResult(
                sucessful = false,
                errorMessage = UIText.StringResource(R.string.empty_tasks_error)
            )
        }
        return ValidationResult(
            sucessful = true
        )
    }
}