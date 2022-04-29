package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.core.Resource
import ch.kra.todo.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class DeleteTodo(
    private val todoRepository: TodoRepository
) {
    operator fun invoke(token: String, todoId: Int): Flow<Resource<String>> {
        return todoRepository.deleteTodo(token, todoId)
    }
}