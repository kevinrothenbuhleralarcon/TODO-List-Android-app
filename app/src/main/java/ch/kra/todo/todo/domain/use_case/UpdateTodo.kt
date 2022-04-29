package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class UpdateTodo(
    private val todoRepository: TodoRepository
) {
    operator fun invoke(token: String, request: AddEditTodoRequestDTO): Flow<Resource<String>> {
        return todoRepository.updateTodo(token, request)
    }
}