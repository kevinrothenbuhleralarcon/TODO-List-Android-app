package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.core.Resource
import ch.kra.todo.todo.domain.model.Todo
import ch.kra.todo.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetTodoList(
    private val todoRepository: TodoRepository
) {
    operator fun invoke(token: String): Flow<Resource<List<Todo>>> {
        return todoRepository.getTodoList(token)
    }
}