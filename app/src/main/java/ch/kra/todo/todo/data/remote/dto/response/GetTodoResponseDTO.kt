package ch.kra.todo.todo.data.remote.dto.response

import ch.kra.todo.todo.data.remote.dto.TodoDTO

data class GetTodoResponseDTO(
    val todo: TodoDTO?
)