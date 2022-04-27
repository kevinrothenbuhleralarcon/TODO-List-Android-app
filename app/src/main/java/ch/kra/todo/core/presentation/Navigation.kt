package ch.kra.todo.core.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.kra.todo.auth.presentation.login.components.LoginScreen
import ch.kra.todo.core.Routes

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                navigate = { event ->
                    navController.navigate(event.route)
                }
            )
        }

        composable(Routes.TODO_LIST){

        }
    }
}