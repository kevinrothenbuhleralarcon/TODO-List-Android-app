package ch.kra.todo.auth.presentation.register.components

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import ch.kra.todo.core.presentation.MainActivity
import ch.kra.todo.core.presentation.Navigation
import ch.kra.todo.core.presentation.ui.theme.TodoTheme
import ch.kra.todo.di.AuthModule
import ch.kra.todo.di.SharedModule
import ch.kra.todo.di.TodoModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ch.kra.todo.R
import ch.kra.todo.auth.presentation.login.components.LoginScreen
import ch.kra.todo.core.Routes
import ch.kra.todo.core.TestTags.API_ERROR
import ch.kra.todo.core.TestTags.CONNECTED_USER
import ch.kra.todo.core.TestTags.EMAIL_ERROR
import ch.kra.todo.core.TestTags.PASSWORD_ERROR
import ch.kra.todo.core.TestTags.REPEATED_PASSWORD_ERROR
import ch.kra.todo.core.TestTags.USERNAME_ERROR
import ch.kra.todo.todo.presentation.todos.components.TodoListScreen

@HiltAndroidTest
@UninstallModules(AuthModule::class, SharedModule::class, TodoModule::class)
class RegisterScreenKtTest {

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
                NavHost(
                    navController = navController,
                    startDestination = Routes.REGISTER
                ) {
                    composable(Routes.REGISTER) {
                        RegisterScreen(
                            navigate = { event ->
                                navController.navigate(event.route) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            },
                            popBackStack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Routes.TODO_LIST) {
                        TodoListScreen(
                            navigate = { event ->
                                if (event.route == Routes.LOGIN) {
                                    navController.navigate(event.route) {
                                        popUpTo(Routes.TODO_LIST) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(event.route)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun onStart_FormIsDisplayed() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).assertExists()
        composeRule.onNodeWithTag(CONNECTED_USER).assertDoesNotExist()

        composeRule.onNodeWithText(context.getString(R.string.register_header)).assertExists()
        composeRule.onNodeWithTag(API_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.username)).assertExists()
        composeRule.onNodeWithTag(USERNAME_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.email)).assertExists()
        composeRule.onNodeWithTag(EMAIL_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.password)).assertExists()
        composeRule.onNodeWithTag(PASSWORD_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.password_2)).assertExists()
        composeRule.onNodeWithTag(REPEATED_PASSWORD_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.password_requirement)).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.submit)).assertExists()

        composeRule.onNodeWithText(context.getString(R.string.author)).assertExists()
    }

    @Test
    fun onSubmitWithNoValue_ErrorMessageAreDisplayed() {
        // Assert that the error fields are not visible
        composeRule.onNodeWithTag(USERNAME_ERROR).assertDoesNotExist()
        composeRule.onNodeWithTag(EMAIL_ERROR).assertDoesNotExist()
        composeRule.onNodeWithTag(PASSWORD_ERROR).assertDoesNotExist()
        composeRule.onNodeWithTag(REPEATED_PASSWORD_ERROR).assertDoesNotExist()

        // Perform click on the submit button
        composeRule.onNodeWithText(context.getString(R.string.submit)).performClick()

        // Assert that the error fields are visible
        composeRule.onNodeWithTag(USERNAME_ERROR).assertExists()
        composeRule.onNodeWithTag(EMAIL_ERROR).assertExists()
        composeRule.onNodeWithTag(PASSWORD_ERROR).assertExists()
        composeRule.onNodeWithTag(REPEATED_PASSWORD_ERROR).assertExists()
    }

    @Test
    fun onSubmitWithExistingAccount_APIErrorMessageIsDisplayed() {
        // Assert that the API error message is not visible
        composeRule.onNodeWithTag(API_ERROR).assertDoesNotExist()

        // Register with existing account and submit
        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput("existing")
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput("test@test.com")
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput("Test1234+")
        composeRule.onNodeWithText(context.getString(R.string.password_2)).performTextInput("Test1234+")
        composeRule.onNodeWithText(context.getString(R.string.submit)).performClick()

        // Assert that the API error message is visible
        composeRule.onNodeWithTag(API_ERROR).assertExists()
    }

    @Test
    fun onSubmitWithCorrectNewUser_NavigateToTodoList() {
        // Register a new account and submit
        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput("success")
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput("test@test.com")
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput("Test1234+")
        composeRule.onNodeWithText(context.getString(R.string.password_2)).performTextInput("Test1234+")
        composeRule.onNodeWithText(context.getString(R.string.submit)).performClick()

        // Assert that we've navigate to TodoList and that the correct username is displayed
        composeRule.onNodeWithText(context.getString(R.string.todo_list_header, "success"))

        composeRule.onNodeWithTag(CONNECTED_USER).assertExists()
        composeRule.onNodeWithTag(CONNECTED_USER).assertTextContains("success")
    }
}