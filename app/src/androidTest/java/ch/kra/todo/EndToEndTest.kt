package ch.kra.todo

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import ch.kra.todo.core.TestTags.TASK_CONTENT
import ch.kra.todo.core.TestTags.TODO_LIST
import ch.kra.todo.core.presentation.MainActivity
import ch.kra.todo.core.presentation.Navigation
import ch.kra.todo.core.presentation.ui.theme.TodoTheme
import ch.kra.todo.di.AuthModule
import ch.kra.todo.di.SharedModule
import ch.kra.todo.di.TodoModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AuthModule::class, SharedModule::class, TodoModule::class)
class EndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent { 
            val navController = rememberNavController()
            TodoTheme {
                Navigation(navController = navController)
            }
        }
    }

    @Test
    fun loginWithCorrectUser_AddNewTodo_UpdateTodo_DeleteTodo() {
        val todoTitle = "Todo 1"
        val todoTitleUpdated = "Todo 1 updated"
        val task1Description = "Task 1"
        val task1DescriptionUpdate = "Task 1 updated"
        val task2Description = "Task 2"

        with(composeRule) {
            // On page login, enter username and password and click submit button
            onNodeWithText(context.getString(R.string.username)).performTextInput("success")
            onNodeWithText(context.getString(R.string.password)).performTextInput("Test1234+")
            onNodeWithText(context.getString(R.string.submit)).performClick()

            // On TodoList page, click the addTodo button
            onNodeWithContentDescription(context.getString(R.string.add_todo)).performClick()

            // On AddEditTodo page, enter todo info and click on save button
            onNodeWithText(context.getString(R.string.title)).performTextInput(todoTitle)
            onNodeWithText(context.getString(R.string.description)).performTextInput(task1Description)

            onNodeWithContentDescription(context.getString(R.string.add_task)).performClick()

            onNodeWithText("").performTextInput(task2Description)

            onNodeWithText(context.getString(R.string.save)).performClick()

            // On TodoList page, check that the todo is correctly added, then click on it
            onNodeWithText(context.getString(R.string.todo_list_header, "success")).assertIsDisplayed()
            onNodeWithText(todoTitle)
                .assertIsDisplayed()
                .performClick()

            // On AddEditTodo page, update the todo
            onNodeWithText(context.getString(R.string.title))
                .assertIsDisplayed()
                //.assertTextEquals(todoTitle)
                .performTextInput(todoTitleUpdated)

            onNodeWithText(task1Description)
                .assertIsDisplayed()
                .performTextInput(task1DescriptionUpdate)


            onRoot()
                .printToLog("task")


            onNodeWithText(task2Description)
                .assertIsDisplayed()
                .onParent()
                .onSiblings()
                .filterToOne(hasContentDescription(context.getString(R.string.remove_task))).performClick()

            onNodeWithText(task2Description).assertDoesNotExist()

            onNodeWithText(context.getString(R.string.save)).performClick()

            // On TodoList page, check that the todo is correctly added, then click on it
            onNodeWithText(todoTitleUpdated)
                .assertIsDisplayed()
                .performClick()

            // On AddEditTodo page, click the delete button
            onNodeWithText(context.getString(R.string.delete)).performClick()
            onNodeWithText(context.getString(R.string.yes)).performClick()

            // On TodoList page assert that there's no todo
            onNodeWithTag(TODO_LIST).assertDoesNotExist()
        }
    }
}