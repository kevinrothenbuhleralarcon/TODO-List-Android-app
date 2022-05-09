package ch.kra.todo.todo.presentation.todos

import app.cash.turbine.test
import ch.kra.todo.MainCoroutineRule
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.data.local.FakeSettingsDataStoreImpl
import ch.kra.todo.todo.data.repository.FakeTodoRepository
import ch.kra.todo.todo.domain.model.Todo
import ch.kra.todo.todo.domain.use_case.GetTodoList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TodoListViewModelTest {

    private lateinit var todoListViewModel: TodoListViewModel
    private lateinit var fakeTodoRepository: FakeTodoRepository
    private lateinit var fakeSettingsDataStoreImpl: FakeSettingsDataStoreImpl

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        fakeTodoRepository = FakeTodoRepository()
        fakeSettingsDataStoreImpl = FakeSettingsDataStoreImpl("success", "success")
        todoListViewModel = TodoListViewModel(
            GetTodoList(fakeTodoRepository),
            fakeSettingsDataStoreImpl
        )
    }

    @Test
    fun `Add todo, UIEvent Navigate to AddEditTodo is send`() = runBlocking {
        todoListViewModel.onEvent(TodoListEvent.AddTodo)
        todoListViewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue("event is not UIEvent.Navigate", event is UIEvent.Navigate)

            if (event is UIEvent.Navigate)
                assertEquals("event.route is not correct", Routes.ADD_EDIT_TODO, event.route)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Edit todo, UIEvent Navigate to AddEditTodo is send with the correct todoId`() = runBlocking {
        val todoId = 3
        todoListViewModel.onEvent(TodoListEvent.EditTodo(todoId = todoId))
        todoListViewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue("event is not UIEvent.Navigate", event is UIEvent.Navigate)

            if (event is UIEvent.Navigate)
                assertEquals("event.route is not correct", Routes.ADD_EDIT_TODO+"?todoId=$todoId", event.route)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Refresh todoList, return updated list of todo`() = runBlocking {
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")
        assertEquals("List of Todo is not empty", 0, todoListViewModel.todoListState.value.todoList.size)
        val todos = listOf(
            Todo(id = 1, title = "Test Todo 1", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 2, title = "Test Todo 2", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 3, title = "Test Todo 3", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 4, title = "Test Todo 4", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 5, title = "Test Todo 5", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList())
        )
        fakeTodoRepository.setUp(todos)

        todoListViewModel.onEvent(TodoListEvent.Refresh)
        assertEquals("List of Todo is not the correct size", 5, todoListViewModel.todoListState.value.todoList.size)
    }


}