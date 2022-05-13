package ch.kra.todo.todo.presentation.add_edit_todo.components

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import ch.kra.todo.R
import ch.kra.todo.core.Routes
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.core.presentation.MainActivity
import ch.kra.todo.core.presentation.ui.theme.TodoTheme
import ch.kra.todo.di.AuthModule
import ch.kra.todo.di.SharedModule
import ch.kra.todo.di.TodoModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AuthModule::class, SharedModule::class, TodoModule::class)
class AddEditScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Inject
    lateinit var fakeSettingsDataStoreImpl: SettingsDataStore

    private val connectedUser = "Kevin"

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()
        fakeSettingsDataStoreImpl.saveTokenToPreferenceStore("success")
        fakeSettingsDataStoreImpl.saveConnectedUserToPreferenceStore(connectedUser)

        composeRule.setContent {
            val navController = rememberNavController()
            TodoTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.ADD_EDIT_TODO
                ) {
                    composable(route = Routes.ADD_EDIT_TODO) {
                        AddEditScreen(
                            navigate = { navController.navigate(it.route) },
                            popBackStack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun onStart_AddTodoIsDisplayed() {
        with(composeRule) {
            onNodeWithContentDescription(context.getString(R.string.back)).assertIsDisplayed()
            onNodeWithText(connectedUser).assertIsDisplayed()

            onNodeWithText(context.getString(R.string.new_todo)).assertIsDisplayed()
            onNodeWithText(context.getString(R.string.title)).assertIsDisplayed()
            onNodeWithText(context.getString(R.string.description)).assertIsDisplayed()
            onNodeWithContentDescription(context.getString(R.string.add_task)).assertIsDisplayed()
            onNodeWithText(context.getString(R.string.save)).assertIsDisplayed()
        }
    }
}