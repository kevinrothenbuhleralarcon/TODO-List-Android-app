package ch.kra.todo.todo.presentation.todos.components

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.core.app.ApplicationProvider
import ch.kra.todo.core.Routes
import ch.kra.todo.core.TestTags.CONNECTED_USER
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.core.presentation.MainActivity
import ch.kra.todo.core.presentation.ui.theme.TodoTheme
import ch.kra.todo.di.AuthModule
import ch.kra.todo.di.SharedModule
import ch.kra.todo.di.TodoModule
import ch.kra.todo.todo.data.remote.FakeTodoApi
import ch.kra.todo.todo.data.remote.TodoApi
import ch.kra.todo.todo.data.remote.dto.TodoDTO
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import ch.kra.todo.R
import ch.kra.todo.core.TestTags.HEADER_TITLE
import ch.kra.todo.core.TestTags.TODO_LIST
import ch.kra.todo.todo.data.remote.dto.TaskDTO
import ch.kra.todo.todo.presentation.add_edit_todo.components.AddEditScreen

@HiltAndroidTest
@UninstallModules(AuthModule::class, SharedModule::class, TodoModule::class)
class TodoListScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeSettingsDataStoreImpl: SettingsDataStore

    @Inject
    lateinit var fakeTodoApi: TodoApi

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val todos =  mutableListOf(
        TodoDTO(id = 1, title = "Test Todo 1", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z", tasks =  listOf(TaskDTO(description = "First task", status = false))),
        TodoDTO(id = 2, title = "Test Todo 2", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z", tasks =  listOf(TaskDTO(description = "First task", status = false))),
        TodoDTO(id = 3, title = "Test Todo 3", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z", tasks =  listOf(TaskDTO(description = "First task", status = false))),
        TodoDTO(id = 4, title = "Test Todo 4", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z", tasks =  listOf(TaskDTO(description = "First task", status = false))),
        TodoDTO(id = 5, title = "Test Todo 5", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z", tasks =  listOf(TaskDTO(description = "First task", status = false)))
    )

    private val connectedUser = "Kevin"

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()
        (fakeTodoApi as FakeTodoApi).todoList = todos
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")
        fakeSettingsDataStoreImpl.saveConnectedUserToPreferenceStore(connectedUser)
        composeRule.setContent {
            val navController = rememberNavController()
            TodoTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.TODO_LIST
                ) {
                    composable(route = Routes.TODO_LIST) {
                        TodoListScreen(
                            navigate = { event ->
                                navController.navigate(event.route)
                            }
                        )
                    }
                    composable(
                        route = Routes.ADD_EDIT_TODO + "?todoId={todoId}",
                        arguments = listOf(
                            navArgument("todoId") {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) {
                        AddEditScreen(
                            navigate = { event ->
                                navController.navigate(event.route)
                            },
                            popBackStack =  {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun onStart_CorrectElementsAreDisplayed() {
        with (composeRule) {
            // Header
            onNodeWithContentDescription(context.getString(R.string.back)).assertDoesNotExist()
            onNodeWithTag(CONNECTED_USER).assertIsDisplayed()
            onNodeWithTag(CONNECTED_USER).assertTextEquals(connectedUser)

            // Main screen
            onNodeWithText(context.getString(R.string.todo_list_header, connectedUser)).assertIsDisplayed()
            // Test that the first todo is displayed
            onNodeWithText(todos.first().title).assertIsDisplayed()
            // Scroll to the last todo because otherwise the node is not on the tree
            onNodeWithTag(TODO_LIST).performScrollToNode(hasText(todos.last().title))
                .onChildren()
                .assertCountEquals(todos.size)
            // Test that the last todo is displayed after the scrolling
            onNodeWithText(todos.last().title).assertIsDisplayed()

            onNodeWithContentDescription(context.getString(R.string.add_todo)).assertIsDisplayed()

            // Footer
            onNodeWithText(context.getString(R.string.author)).assertIsDisplayed()
        }
    }

    @Test
    fun onAddTodoClick_NavigateToAddEditTodoThenOnBackPressed_NavigateBackToTodoList() {
        with(composeRule) {
            onNodeWithContentDescription(context.getString(R.string.add_todo)).performClick()
            // Assert that we're on the AddEditTodoScreen with a new empty todo
            onNodeWithText(context.getString(R.string.new_todo)).assertIsDisplayed()
            onNodeWithText(context.getString(R.string.title)).assertIsDisplayed()
            onNodeWithText(context.getString(R.string.description)).assertIsDisplayed()
            // As it's a new todo only one task is present so the remove task button should not be present
            onNodeWithContentDescription(context.getString(R.string.remove_task)).assertDoesNotExist()

            // Press the navigate back button
            onNodeWithContentDescription(context.getString(R.string.back)).performClick()

            // Assert that we're back on the todoListScreen
            onNodeWithText(context.getString(R.string.todo_list_header, connectedUser)).assertIsDisplayed()
        }
    }

    @Test
    fun onTodoClick_NavigateToAddEditTodo() {
        with(composeRule) {
            onNodeWithText(todos.first().title).performClick()

            // Assert that we're on the AddEditTodoScreen with the correct todo loaded
            onNodeWithTag(HEADER_TITLE).assertTextEquals(todos.first().title)
            onNodeWithText(todos.first().tasks!![0].description).assertIsDisplayed()
        }
    }
}