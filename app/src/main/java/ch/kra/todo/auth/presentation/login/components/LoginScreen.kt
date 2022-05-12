package ch.kra.todo.auth.presentation.login.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.kra.todo.R
import ch.kra.todo.auth.presentation.login.LoginListEvent
import ch.kra.todo.auth.presentation.login.LoginViewModel
import ch.kra.todo.core.TestTags.API_ERROR
import ch.kra.todo.core.TestTags.PASSWORD_ERROR
import ch.kra.todo.core.TestTags.USERNAME_ERROR
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.presentation.ui.shared_composable.Footer
import ch.kra.todo.core.presentation.ui.shared_composable.Header
import ch.kra.todo.core.presentation.ui.shared_composable.LoadingWrapper
import ch.kra.todo.core.presentation.ui.shared_composable.TodoCard
import ch.kra.todo.core.presentation.ui.theme.TextErrorColor
import kotlinx.coroutines.flow.collect

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigate: (UIEvent.Navigate) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val loginFormState = viewModel.loginFormState.value

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> navigate(event)
                is UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                is UIEvent.PopBackStack -> {}
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
                text = stringResource(R.string.welcome),
                fontSize = 32.sp,
                fontWeight = Bold
            )
            LoadingWrapper(
                isLoading = loginFormState.isLoading,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TodoCard {
                    LoginForm(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginFormState = viewModel.loginFormState.value
    val error = viewModel.apiError.value

    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.login_header),
            fontSize = 24.sp,
            fontWeight = Bold
        )
        if (error.asString().isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .testTag(API_ERROR),
                text = error.asString(),
                fontSize = 16.sp,
                fontWeight = Bold,
                color = TextErrorColor
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = loginFormState.username,
            onValueChange = {
                viewModel.onEvent(LoginListEvent.EnteredUsername(it))
            },
            label = { Text(text = stringResource(R.string.username)) },
            singleLine = true,
            isError = loginFormState.usernameError != null
        )

        if (loginFormState.usernameError != null) {
            Text(
                text = loginFormState.usernameError.asString(),
                color = MaterialTheme.colors.error,
                modifier = Modifier
                    .testTag(USERNAME_ERROR)
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = loginFormState.password,
            onValueChange = {
                viewModel.onEvent(LoginListEvent.EnteredPassword(it))
            },
            label = { Text(text = stringResource(R.string.password)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (loginFormState.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (loginFormState.passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (loginFormState.passwordVisibility)
                    stringResource(R.string.hide_password)
                else stringResource(R.string.show_password)
                IconButton(onClick = { viewModel.onEvent(LoginListEvent.TogglePasswordVisibility) }) {
                    Icon(
                        imageVector = image,
                        contentDescription = description
                    )
                }
            },
            isError = loginFormState.passwordError != null
        )

        if (loginFormState.passwordError != null) {
            Text(
                text = loginFormState.passwordError.asString(),
                color = MaterialTheme.colors.error,
                modifier = Modifier
                    .testTag(PASSWORD_ERROR)
            )
        }

        Button(
            onClick = { viewModel.onEvent(LoginListEvent.Login) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.submit),
                fontSize = 18.sp
            )
        }
        val annotatedLinkString: AnnotatedString = buildAnnotatedString {
            val str = stringResource(R.string.create_account)
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
        }
        ClickableText(
            modifier = Modifier.fillMaxWidth(),
            text = annotatedLinkString,
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
            onClick = {
                viewModel.onEvent(LoginListEvent.Register)
            }
        )
    }
}