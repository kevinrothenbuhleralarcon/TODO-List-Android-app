package ch.kra.todo.core.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ch.kra.todo.auth.presentation.login.components.LoginScreen
import ch.kra.todo.auth.presentation.register.components.RegisterScreen
import ch.kra.todo.core.Routes
import ch.kra.todo.todo.presentation.add_edit_todo.components.AddEditScreen
import ch.kra.todo.todo.presentation.todos.components.TodoListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                navigate = { event ->
                    if (event.route == Routes.TODO_LIST) {
                        navController.navigate(event.route) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(event.route)
                    }

                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                navigate = { event ->
                    navController.navigate(event.route) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                popBackStack =  {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.TODO_LIST) {
            TodoListScreen(
                navigate = { event->
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
                    if(event.route == Routes.LOGIN) {
                        navController.navigate(event.route) {
                            popUpTo(Routes.TODO_LIST) { inclusive = true }
                        }
                    } else {
                        navController.navigate(event.route)
                    }
                },
                popBackStack =  {
                    navController.popBackStack()
                }
            )
        }
    }
}