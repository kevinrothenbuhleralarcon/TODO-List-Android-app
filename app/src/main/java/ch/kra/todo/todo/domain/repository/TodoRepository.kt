package ch.kra.todo.todo.domain.repository

import ch.kra.todo.core.Resource
import ch.kra.todo.todo.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodoList(token: String): Flow<Resource<List<Todo>>>
}