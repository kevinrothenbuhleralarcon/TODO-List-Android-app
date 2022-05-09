package ch.kra.todo.todo.data.remote.dto

import ch.kra.todo.core.DateFormatUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskDTOTest {

    @Test
    fun `To Task, return Task`() {
        val taskDTO = TaskDTO(
            description = "Task 1",
            deadline = "2022-05-02T06:53:22.231Z",
            status = false
        )

        val task = taskDTO.toTask()

        assertEquals("Description is not correct", taskDTO.description, task.description)
        assertEquals("Status is not correct", taskDTO.status, task.status)
        assertEquals("Deadline is not correct", DateFormatUtil.fromISOInstantString(taskDTO.deadline!!), task.deadline)
    }

    @Test
    fun `To Task with incorrect date, return Task with null deadline`() {
        val taskDTO = TaskDTO(
            description = "Task 1",
            deadline = "05.02.2022",
            status = false
        )

        val task = taskDTO.toTask()

        assertEquals("Description is not correct", taskDTO.description, task.description)
        assertEquals("Status is not correct", taskDTO.status, task.status)
        assertNull("Deadline is not null", task.deadline)
    }
}