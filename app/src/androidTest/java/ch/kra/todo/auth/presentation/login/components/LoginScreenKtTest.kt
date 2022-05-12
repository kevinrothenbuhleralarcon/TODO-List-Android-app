package ch.kra.todo.auth.presentation.login.components

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ch.kra.todo.core.presentation.MainActivity
import ch.kra.todo.di.AuthModule
import ch.kra.todo.di.SharedModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
@UninstallModules(AuthModule::class, SharedModule::class)
class LoginScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {

        }
    }
}