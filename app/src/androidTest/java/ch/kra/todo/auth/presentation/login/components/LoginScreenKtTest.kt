package ch.kra.todo.auth.presentation.login.components

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import ch.kra.todo.R
import ch.kra.todo.core.TestTags.API_ERROR
import ch.kra.todo.core.TestTags.CONNECTED_USER
import ch.kra.todo.core.TestTags.PASSWORD_ERROR
import ch.kra.todo.core.TestTags.USERNAME_ERROR
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
class LoginScreenKtTest {

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
    fun onStart_FormIsDisplayed() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).assertDoesNotExist()
        composeRule.onNodeWithTag(CONNECTED_USER).assertDoesNotExist()

        composeRule.onNodeWithText(context.getString(R.string.login_header)).assertExists()
        composeRule.onNodeWithTag(API_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.username)).assertExists()
        composeRule.onNodeWithTag(USERNAME_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.password)).assertExists()
        composeRule.onNodeWithTag(PASSWORD_ERROR).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.submit)).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.create_account)).assertExists()

        composeRule.onNodeWithText(context.getString(R.string.author)).assertExists()
    }

    @Test
    fun onSubmitClickWithNoValue_ErrorMessageAreDisplayed() {
        // Assert that the error text are not displayed
        composeRule.onNodeWithTag(USERNAME_ERROR).assertDoesNotExist()
        composeRule.onNodeWithTag(PASSWORD_ERROR).assertDoesNotExist()

        // Perform click on the submit button
        composeRule.onNodeWithText(context.getString(R.string.submit)).performClick()

        // Assert that the error text are displayed
        composeRule.onNodeWithTag(USERNAME_ERROR).assertExists()
        composeRule.onNodeWithTag(PASSWORD_ERROR).assertExists()
    }

    @Test
    fun onSubmitClickWithWrongCredentials_APIErrorMessageIsDisplayed() {
        composeRule.onNodeWithTag(API_ERROR).assertDoesNotExist()

        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput("notSuccess")
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput("Password")
        composeRule.onNodeWithText(context.getString(R.string.submit)).performClick()

        composeRule.onNodeWithTag(API_ERROR).assertExists()
    }

    @Test
    fun onSubmitClickWithCorrectCredentials_NavigateToTodoList() {
        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput("success")
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput("Password")
        composeRule.onNodeWithText(context.getString(R.string.submit)).performClick()

        composeRule.onNodeWithText(context.getString(R.string.todo_list_header, "success")).assertExists()
    }

    @Test
    fun onCreateAccountClick_NavigateToRegister() {
        // Assert that we're on the login screen
        composeRule.onNodeWithText(context.getString(R.string.login_header)).assertExists()

        // Click on the link to navigate on the register screen
        composeRule.onNodeWithText(context.getString(R.string.create_account)).performClick()

        // Assert that we're on the register screen
        composeRule.onNodeWithText(context.getString(R.string.register_header)).assertExists()
    }

    @Test
    fun onCreateAccountClickAndBack_NavigateToRegisterThenPopBackToLogin() {
        // Assert that we're on the login screen
        composeRule.onNodeWithText(context.getString(R.string.login_header)).assertExists()

        // Click on the link to navigate on the register screen
        composeRule.onNodeWithText(context.getString(R.string.create_account)).performClick()

        // Assert that we're on the register screen
        composeRule.onNodeWithText(context.getString(R.string.register_header)).assertExists()

        // Click on the navigate back arrow
        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).performClick()

        // Assert that we're back on the login screen
        composeRule.onNodeWithText(context.getString(R.string.login_header)).assertExists()
    }
}