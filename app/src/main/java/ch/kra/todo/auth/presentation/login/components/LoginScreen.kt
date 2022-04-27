package ch.kra.todo.auth.presentation.login.components


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.kra.todo.auth.presentation.login.AuthListEvent
import ch.kra.todo.auth.presentation.login.LoginViewModel
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.presentation.TodoCard
import ch.kra.todo.core.presentation.Footer
import ch.kra.todo.core.presentation.Header
import kotlinx.coroutines.flow.collect

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigate: (UIEvent.Navigate) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> navigate(event)

                is UIEvent.DisplayError -> {

                }

                is UIEvent.DisplayLoading -> {

                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            Footer(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
            )
        },
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth()
            )
            Text(
                modifier = Modifier
                    .padding(20.dp),
                text = "Welcome to Todo list",
                fontSize = 32.sp,
                fontWeight = Bold
            )
            TodoCard() {
                LoginForm(
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val username = viewModel.username.value
    val password = viewModel.password.value
    val passwordVisible = viewModel.passwordVisible.value

    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center,
            text = "Please sign in",
            fontSize = 24.sp,
            fontWeight = Bold
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = username,
            onValueChange = {
                viewModel.onEvent(AuthListEvent.EnteredUsername(it))
            },
            label = { Text(text = "Username") },
            singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = password,
            onValueChange = {
                viewModel.onEvent(AuthListEvent.EnteredPassword(it))
            },
            label = { Text(text = "Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { viewModel.onEvent(AuthListEvent.TogglePasswordVisibility) }) {
                    Icon(
                        imageVector = image,
                        contentDescription = description)
                }
            }
        )
        Button(
            onClick = { viewModel.onEvent(AuthListEvent.Login) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = "Submit",
                fontSize = 18.sp
            )
        }
        val annotatedLinkString: AnnotatedString = buildAnnotatedString {
            val str = "Create account"
            val startIndex = 0
            val endIndex = str.length
            append(str)
            addStyle(
                style = SpanStyle(
                    color = Color(0xff64B5F6),
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                ), start = startIndex, end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = "url",
                start = startIndex,
                end = endIndex
            )
        }
        ClickableText(
            modifier = Modifier.fillMaxWidth(),
            text = annotatedLinkString,
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
            onClick = {
                annotatedLinkString
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        viewModel.onEvent(AuthListEvent.OnNavigateToWebClient(stringAnnotation.item))
                    }
            }
        )
    }
}