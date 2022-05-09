package ch.kra.todo.todo.data.remote.dto

import ch.kra.todo.core.DateFormatUtil
import org.junit.Assert.*
import org.junit.Test
import java.util.zip.DataFormatException

class TodoDTOTest {

    @Test
    fun `To Todo no tasks, return new Todo`() {
        val todoDTO = TodoDTO(
            id = 1,
            title = "Todo 1",
            createdAt = "2022-05-02T06:53:22.231Z",
            lastUpdatedAt = "2022-05-02T06:53:22.231Z"
        )
        val todo = todoDTO.toTodo()
        assertEquals("Id is not correct", todoDTO.id, todo.id)
        assertEquals("Title is not correct", todoDTO.title, todo.title)
        assertEquals("Created at is not correct", DateFormatUtil.fromISOInstantString(todoDTO.createdAt), todo.createdAt)
        assertEquals("Last updated at is not correct", DateFormatUtil.fromISOInstantString(todoDTO.lastUpdatedAt), todo.lastUpdatedAt)
    }

    @Test
    fun `To Todo invalid dated, return new Todo with currentDate`() {
        val todoDTO = TodoDTO(
            id = 1,
            title = "Todo 1",
            createdAt = "05.19.2022",
            lastUpdatedAt = "05.1.2019"
        )
        val todo = todoDTO.toTodo()
        assertEquals("Id is not correct", todoDTO.id, todo.id)
        assertEquals("Title is not correct", todoDTO.title, todo.title)
        assertNotNull("Created at is not correct", todo.createdAt)
        assertNotNull("Last updated at is not correct", todo.lastUpdatedAt)
    }
}