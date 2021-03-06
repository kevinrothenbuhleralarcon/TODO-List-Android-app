package ch.kra.todo.todo.presentation.add_edit_todo

import android.content.Context
import androidx.compose.animation.defaultDecayAnimationSpec
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import ch.kra.todo.MainCoroutineRule
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.Resource
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.data.local.FakeSettingsDataStoreImpl
import ch.kra.todo.todo.data.repository.FakeTodoRepository
import ch.kra.todo.todo.domain.model.Task
import ch.kra.todo.todo.domain.model.Todo
import ch.kra.todo.todo.domain.use_case.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class AddEditTodoViewModelTest {

    private lateinit var addEditTodoViewModel: AddEditTodoViewModel
    private lateinit var fakeTodoRepository: FakeTodoRepository
    private lateinit var  fakeSettingsDataStoreImpl: FakeSettingsDataStoreImpl
    private  val todos = listOf(
        Todo(id = 1, title = "Test Todo 1", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  listOf(Task(description = "", status = false))),
        Todo(id = 2, title = "Test Todo 2", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  listOf(Task(description = "", status = false))),
        Todo(id = 3, title = "Test Todo 3", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  listOf(Task(description = "", status = false))),
        Todo(id = 4, title = "Test Todo 4", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  listOf(Task(description = "", status = false))),
        Todo(id = 5, title = "Test Todo 5", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  listOf(Task(description = "", status = false)))
    )

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainCoroutineRule()

    @Before
    fun setUp() {
        fakeTodoRepository = FakeTodoRepository()
        fakeTodoRepository.setUp(todos)
        fakeSettingsDataStoreImpl = FakeSettingsDataStoreImpl()
    }

    private fun initViewModel(savedStateHandle: SavedStateHandle) {
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
    }

    private fun validateNavigateToLogin() = runBlocking {
        addEditTodoViewModel.uiEvent.test {
            val event = awaitItem()
            if (event is UIEvent.Navigate)
                assertEquals("The route is not correct", Routes.LOGIN, event.route)
            else
                fail("event is not navigate")
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun validatePopBackStack() = runBlocking {
        addEditTodoViewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue("event id not PopBackStack", event is UIEvent.PopBackStack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Get Todo init with correct id and token, return todo`() = runBlocking {
        val todoId = 2
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", todoId)
        }

        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")

        initViewModel(savedStateHandle)
        assertEquals("currentTodoId is not correct", todoId, addEditTodoViewModel.currentTodoId)
        assertEquals("Todo title is not correct", todos[todoId-1].title, addEditTodoViewModel.todoFormState.value.title)
    }

    @Test
    fun `Get Todo init with correct id and incorrect token, Navigate UIEvent to login is send`() = runBlocking {
        val todoId = 2
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", todoId)
        }
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("notSuccess")

        initViewModel(savedStateHandle)
        validateNavigateToLogin()
    }

    @Test
    fun `Get Todo init with incorrect todoId, ShowSnackbar UIEvent is send`() = runBlocking {
        val todoId = 10
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", todoId)
        }

        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")

        initViewModel(savedStateHandle)

        addEditTodoViewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue("event is not ShowSnackbar", event is UIEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Add Todo init, todoFormState is empty`() = runBlocking {
        val savedStateHandle = SavedStateHandle()
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")

        initViewModel(savedStateHandle)

        assertEquals("Todo title is not empty", "", addEditTodoViewModel.todoFormState.value.title)
    }

    @Test
    fun `Save Todo with form error, return todoFormState with errors`() = runBlocking {
        val savedStateHandle = SavedStateHandle()

        initViewModel(savedStateHandle)

        assertNull("titleError is not null", addEditTodoViewModel.todoFormState.value.titleError)
        assertNull("taskEmptyError is not null", addEditTodoViewModel.todoFormState.value.tasksEmptyError)
        addEditTodoViewModel.todoFormState.value.tasks.forEach {
            assertNull("descriptionError is not null", it.descriptionError)
        }

        addEditTodoViewModel.onEvent(AddEditTodoEvent.Save)

        assertNotNull("titleError is null", addEditTodoViewModel.todoFormState.value.titleError)
        addEditTodoViewModel.todoFormState.value.tasks.forEach {
            assertNotNull("descriptionError null", it.descriptionError)
        }
    }

    @Test
    fun `Save Todo from getTodo with valid token, todo is updated and PopBackStack UIEvent is send`() = runBlocking {
        val todoId = 3
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", todoId)
        }

        val token = "success"

        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore(token)

        initViewModel(savedStateHandle)
        val title = "Updated title"
        val description0 = "First task description"
        val description1 = "Second task description"
        val deadline1 = DateFormatUtil.fromDateTimeValues(2022, 5, 12)
        val status1 = true
        addEditTodoViewModel.onEvent(AddEditTodoEvent.TitleChanged(title))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(0, description0))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.AddTask)
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(1, description1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DeadlineChanged(1, deadline1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.StatusChanged(1, status1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.Save)

        validatePopBackStack()

        fakeTodoRepository.getTodo(token, todoId).test {
            val loading = awaitItem()
            assertTrue("loading is not Loading", loading is Resource.Loading)
            val resource = awaitItem()
            assertTrue("resource is not Success", resource is Resource.Success)
            if (resource is Resource.Success) {
                val todo = resource.data
                assertEquals("id is not correct", todoId, todo.id)
                assertEquals("title is not correct", title, todo.title)
                assertEquals("Description of the first task is not correct", description0,
                    todo.tasks?.get(0)?.description
                )
                assertEquals("Description of the second task is not correct", description1,
                    todo.tasks?.get(1)?.description
                )
                assertEquals("Deadline of the second task is not correct", deadline1,
                    todo.tasks?.get(1)?.deadline
                )
                assertEquals("status of the second task is not correct", status1,
                    todo.tasks?.get(1)?.status
                )
            }
            awaitComplete()
        }
    }

    @Test
    fun `Save Todo from getTodo with invalid token, UIEvent Navigate to route Login is send`() = runBlocking {
        val todoId = 3
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", todoId)
        }

        val token = "notSuccess"

        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore(token)

        initViewModel(savedStateHandle)
        val title = "Updated title"
        val description0 = "First task description"
        val description1 = "Second task description"
        val deadline1 = DateFormatUtil.fromDateTimeValues(2022, 5, 12)
        val status1 = true
        addEditTodoViewModel.onEvent(AddEditTodoEvent.TitleChanged(title))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(0, description0))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.AddTask)
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(1, description1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DeadlineChanged(1, deadline1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.StatusChanged(1, status1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.Save)

        validateNavigateToLogin()
    }

    @Test
    fun `Save Todo from addTodo with valid token, todo is added and PopBackStack UIEvent is send`() = runBlocking {
        val todoId = todos.size + 1
        val savedStateHandle = SavedStateHandle()

        val token = "success"

        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore(token)

        initViewModel(savedStateHandle)
        val title = "Todo $todoId"
        val description0 = "First task description"
        val description1 = "Second task description"
        val deadline1 = DateFormatUtil.fromDateTimeValues(2022, 5, 12)
        val status1 = true
        addEditTodoViewModel.onEvent(AddEditTodoEvent.TitleChanged(title))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(0, description0))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.AddTask)
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(1, description1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DeadlineChanged(1, deadline1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.StatusChanged(1, status1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.Save)

        validatePopBackStack()

        fakeTodoRepository.getTodo(token, todoId).test {
            val loading = awaitItem()
            assertTrue("loading is not Loading", loading is Resource.Loading)
            val success = awaitItem()
            assertTrue("success is not Success", success is Resource.Success)
            if (success is Resource.Success) {
                val todo = success.data
                assertEquals("id is not correct", todoId, todo.id)
                assertEquals("title is not correct", title, todo.title)
                assertEquals("Description of the first task is not correct", description0,
                    todo.tasks?.get(0)?.description
                )
                assertEquals("Description of the second task is not correct", description1,
                    todo.tasks?.get(1)?.description
                )
                assertEquals("Deadline of the second task is not correct", deadline1,
                    todo.tasks?.get(1)?.deadline
                )
                assertEquals("status of the second task is not correct", status1,
                    todo.tasks?.get(1)?.status
                )
            }
            awaitComplete()
        }
    }

    @Test
    fun `Save Todo from addTodo with invalid token, UIEvent Navigate to route Login is send`() = runBlocking {
        val savedStateHandle = SavedStateHandle()

        val token = "notSuccess"

        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore(token)

        initViewModel(savedStateHandle)
        val title = "Updated title"
        val description0 = "First task description"
        val description1 = "Second task description"
        val deadline1 = DateFormatUtil.fromDateTimeValues(2022, 5, 12)
        val status1 = true
        addEditTodoViewModel.onEvent(AddEditTodoEvent.TitleChanged(title))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(0, description0))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.AddTask)
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(1, description1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DeadlineChanged(1, deadline1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.StatusChanged(1, status1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.Save)

        validateNavigateToLogin()
    }

    @Test
    fun `Delete Todo from addTodo, Nothing is done`() = runBlocking {
        val mockContext = mock<Context>()
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.Delete)
        assertFalse("isLoading is not false", addEditTodoViewModel.todoFormState.value.isLoading)
        assertEquals("ApiError is not empty", "", addEditTodoViewModel.apiError.value.asString(mockContext))
        addEditTodoViewModel.uiEvent.test {
            try {
                awaitItem()
                fail("Timeout exception expected")
            } catch (e: TimeoutCancellationException) {
                assertEquals("exception is not correct", "Timed out waiting for 1000 ms", e.message)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Delete Todo getTodo invalid token, UIEvent Navigate to route Login is send`() = runBlocking {
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", 3)
        }
        initViewModel(savedStateHandle)
        addEditTodoViewModel.onEvent(AddEditTodoEvent.Delete)
        validateNavigateToLogin()
    }

    @Test
    fun `Delete Todo getTodo valid token invalid id, Nothing is done`() = runBlocking {
        val mockContext = mock<Context>()
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", 1000)
        }
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")
        initViewModel(savedStateHandle)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.Delete)
        assertFalse("isLoading is not false", addEditTodoViewModel.todoFormState.value.isLoading)
        assertEquals("ApiError is not empty", "", addEditTodoViewModel.apiError.value.asString(mockContext))
        addEditTodoViewModel.uiEvent.test {
            val snackbar = awaitItem()
            if (snackbar is UIEvent.ShowSnackbar)
                assertEquals("message is not correct", "No todo with this id", snackbar.message.asString(mockContext))
            else
                fail("snackbar is not ShowSnackbar")

            try {
                awaitItem()
                fail("Timeout exception expected")
            } catch (e: TimeoutCancellationException) {
                assertEquals("exception is not correct", "Timed out waiting for 1000 ms", e.message)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Delete Todo getTodo valid token valid id, Todo is deleted and popBackStack UIEvent is send`() = runBlocking {
        val todoId = 3
        val savedStateHandle = SavedStateHandle().apply {
            set("todoId", todoId)
        }
        val token = "success"
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore(token)
        initViewModel(savedStateHandle)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.Delete)

        validatePopBackStack()

        fakeTodoRepository.getTodoList(token).test {
            val loading = awaitItem()
            assertTrue("loading is not Loading", loading is Resource.Loading)

            val success = awaitItem()
            if (success is Resource.Success) {
                val todoList = success.data
                assertEquals("todoList is not the correct size", todos.size - 1, todoList.size)
                assertNull("todo has not been deleted", todoList.find { it.id == todoId })
            } else fail("resource is not Success")
            awaitComplete()
        }
    }

    @Test
    fun `Navigate back, UIEvent PopBackStack is send`() = runBlocking {
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.NavigateBack)


    }

    @Test
    fun `TitleChanged, todo title is changed`() {
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        assertEquals("Title is not empty", "", addEditTodoViewModel.todoFormState.value.title)

        val title = "Todo title"
        addEditTodoViewModel.onEvent(AddEditTodoEvent.TitleChanged(title))

        assertEquals("Title is not correct", title, addEditTodoViewModel.todoFormState.value.title)
    }

    @Test
    fun `DescriptionChanged, task description is changed`() {
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        assertEquals("description is not empty", "", addEditTodoViewModel.todoFormState.value.tasks[0].description)

        val description = "Task description"
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(0, description))

        assertEquals("Description is not correct", description, addEditTodoViewModel.todoFormState.value.tasks[0].description)
    }

    @Test
    fun `StatusChanged, task status is changed`() {
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        assertFalse("status is not false", addEditTodoViewModel.todoFormState.value.tasks[0].status)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.StatusChanged(0, true))

        assertTrue("status is not true", addEditTodoViewModel.todoFormState.value.tasks[0].status)
    }

    @Test
    fun `DeadlineChanged, task deadline is changed`() {
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        assertNull("deadline is not null", addEditTodoViewModel.todoFormState.value.tasks[0].deadline)

        val deadline = DateFormatUtil.fromDateTimeValues(2022, 5, 12)
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DeadlineChanged(0, deadline))

        assertEquals("deadline is not correct", deadline, addEditTodoViewModel.todoFormState.value.tasks[0].deadline)
    }

    @Test
    fun `AddTask, another empty task is added`() {
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        val numberOfTasks = 1
        assertEquals("number of tasks is not correct", numberOfTasks, addEditTodoViewModel.todoFormState.value.tasks.size)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.AddTask)

        assertEquals("number of tasks is not correct", numberOfTasks + 1, addEditTodoViewModel.todoFormState.value.tasks.size)
    }

    @Test
    fun `RemoveTask, the correct task is removed`() {
        val savedStateHandle = SavedStateHandle()
        initViewModel(savedStateHandle)

        val description1 = "Task 1"
        val description2 = "Task 2"
        val description3 = "Task 3"

        var numberOfTasks = 1
        assertEquals("number of tasks is not correct", numberOfTasks, addEditTodoViewModel.todoFormState.value.tasks.size)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.AddTask)
        numberOfTasks++
        addEditTodoViewModel.onEvent(AddEditTodoEvent.AddTask)
        numberOfTasks++

        assertEquals("number of tasks is not correct", numberOfTasks, addEditTodoViewModel.todoFormState.value.tasks.size)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(0, description1))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(1, description2))
        addEditTodoViewModel.onEvent(AddEditTodoEvent.DescriptionChanged(2, description3))

        addEditTodoViewModel.onEvent(AddEditTodoEvent.RemoveTask(1))
        numberOfTasks--

        assertEquals("number of tasks is not correct", numberOfTasks, addEditTodoViewModel.todoFormState.value.tasks.size)
        assertEquals("task at id 1 has not the correct description", description3, addEditTodoViewModel.todoFormState.value.tasks[1].description)

        addEditTodoViewModel.onEvent(AddEditTodoEvent.RemoveTask(0))
        numberOfTasks--

        assertEquals("number of tasks is not correct", numberOfTasks, addEditTodoViewModel.todoFormState.value.tasks.size)
        assertEquals("task at id 1 has not the correct description", description3, addEditTodoViewModel.todoFormState.value.tasks[0].description)

        /* this is the last task, it should not be removed */
        addEditTodoViewModel.onEvent(AddEditTodoEvent.RemoveTask(0))
        assertEquals("number of tasks is not correct", numberOfTasks, addEditTodoViewModel.todoFormState.value.tasks.size)
    }
}