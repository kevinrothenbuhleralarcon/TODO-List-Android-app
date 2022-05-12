package ch.kra.todo.auth.presentation.register.components

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import ch.kra.todo.core.TestTags.API_ERROR
import ch.kra.todo.core.TestTags.CONNECTED_USER
import ch.kra.todo.core.TestTags.EMAIL_ERROR
import ch.kra.todo.core.TestTags.PASSWORD_ERROR
import ch.kra.todo.core.TestTags.REPEATED_PASSWORD_ERROR
import ch.kra.todo.core.TestTags.USERNAME_ERROR

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
                Navigation(navController = navController)
            }
        }
    }

    @Test
    fun onStart_FormIsDisplayed() {
        composeRule.onNodeWithText(context.getString(R.string.create_account)).performClick()
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
}