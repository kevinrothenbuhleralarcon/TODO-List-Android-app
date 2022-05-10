package ch.kra.todo.todo.presentation.add_edit_todo

import androidx.lifecycle.SavedStateHandle
import ch.kra.todo.MainCoroutineRule
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.data.local.FakeSettingsDataStoreImpl
import ch.kra.todo.todo.data.repository.FakeTodoRepository
import ch.kra.todo.todo.domain.model.Todo
import ch.kra.todo.todo.domain.use_case.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AddEditTodoViewModelTest {

    private lateinit var addEditTodoViewModel: AddEditTodoViewModel
    private lateinit var fakeTodoRepository: FakeTodoRepository
    private lateinit var  fakeSettingsDataStoreImpl: FakeSettingsDataStoreImpl

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainCoroutineRule()

    @Before
    fun setUp() {
        fakeTodoRepository = FakeTodoRepository()
        fakeSettingsDataStoreImpl = FakeSettingsDataStoreImpl()
    }

    @Test
    fun `Get Todo with correct id and token, return todo`() = runBlocking {
        val todoId = 2
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", todoId)
        }
        val todos = listOf(
            Todo(id = 1, title = "Test Todo 1", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 2, title = "Test Todo 2", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 3, title = "Test Todo 3", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 4, title = "Test Todo 4", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 5, title = "Test Todo 5", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList())
        )
        fakeTodoRepository.setUp(todos)
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")

        addEditTodoViewModel = AddEditTodoViewModel(
            GetTodo(fakeTodoRepository),
            fakeSettingsDataStoreImpl,
            AddTodo(fakeTodoRepository),
            UpdateTodo(fakeTodoRepository),
            DeleteTodo(fakeTodoRepository),
            ValidateTaskDescription(),
            ValidateTodoTitle(),
            ValidateTaskEmpty(),
            savedStateHandle
        )

        assertEquals("Todo title is not correct", todos[todoId-1].title, addEditTodoViewModel.todoFormState.value.title)
    }
}