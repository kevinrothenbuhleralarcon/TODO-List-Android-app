package ch.kra.todo.auth.presentation.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.kra.todo.R
import ch.kra.todo.auth.presentation.register.RegisterListEvent
import ch.kra.todo.auth.presentation.register.RegisterViewModel
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.presentation.ui.shared_composable.Footer
import ch.kra.todo.core.presentation.ui.shared_composable.Header
import ch.kra.todo.core.presentation.ui.shared_composable.LoadingWrapper
import ch.kra.todo.core.presentation.ui.shared_composable.TodoCard
import ch.kra.todo.core.presentation.ui.theme.TextErrorColor
import ch.kra.todo.core.presentation.ui.theme.TextInfoColor
import kotlinx.coroutines.flow.collect

@Composable
fun RegisterScreen(
     viewModel: RegisterViewModel = hiltViewModel(),
     navigate: (UIEvent.Navigate) -> Unit,
     popBackStack: () -> Unit
) {
     val registerFormState = viewModel.registerFormState.value

     val scaffoldState = rememberScaffoldState()
     val context = LocalContext.current

     LaunchedEffect(key1 = true) {
          viewModel.uiEvent.collect { event ->
               when (event) {
                    is UIEvent.Navigate -> navigate(event)

                    is UIEvent.PopBackStack -> popBackStack()

                    is UIEvent.ShowSnackbar -> {
                         scaffoldState.snackbarHostState.showSnackbar(
                              event.message.asString(context)
                         )
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
                    hasBackArrow = true,
                    modifier = Modifier
                         .height(46.dp)
                         .fillMaxWidth(),
                    navigateBack = { viewModel.onEvent(RegisterListEvent.NavigateBack) },
               )
               Text(
                    modifier = Modifier
                         .padding(20.dp),
                    text = stringResource(R.string.welcome),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
               )
               LoadingWrapper(
                    isLoading = registerFormState.isLoading,
                    modifier = Modifier.fillMaxSize()
               ) {
                    TodoCard {
                         RegisterForm(
                              modifier = Modifier
                                   .fillMaxWidth()
                                   .verticalScroll(rememberScrollState())
                         )
                    }
               }
          }
     }
}

@Composable
private fun RegisterForm(
     modifier: Modifier = Modifier,
     viewModel: RegisterViewModel = hiltViewModel()
) {
     val registerFormState = viewModel.registerFormState.value
     val error = viewModel.apiError.value

     Column(modifier = modifier) {
          Text(
               modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
               textAlign = TextAlign.Center,
               text = stringResource(R.string.register_header),
               fontSize = 24.sp,
               fontWeight = FontWeight.Bold
          )
          if (error.asString().isNotEmpty()) {
               Text(
                    modifier= Modifier
                         .fillMaxWidth()
                         .padding(vertical = 10.dp),
                    text = error.asString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextErrorColor
               )
          }

          OutlinedTextField(
               modifier = Modifier
                    .fillMaxWidth(),
               value = registerFormState.username,
               onValueChange = {
                    viewModel.onEvent(RegisterListEvent.EnteredUsername(it))
               },
               label = { Text(text = stringResource(R.string.username)) },
               singleLine = true,
               isError = registerFormState.usernameError != null
          )

          if (registerFormState.usernameError != null) {
               Text(
                    text = registerFormState.usernameError.asString(),
                    color = MaterialTheme.colors.error
               )
          }

          OutlinedTextField(
               modifier = Modifier
                    .fillMaxWidth(),
               value = registerFormState.email,
               onValueChange = {
                    viewModel.onEvent(RegisterListEvent.EnteredEmail(it))
               },
               label = { Text(text = stringResource(R.string.email)) },
               singleLine = true,
               isError = registerFormState.emailError != null
          )

          if (registerFormState.emailError != null) {
               Text(
                    text = registerFormState.emailError.asString(),
                    color = MaterialTheme.colors.error
               )
          }

          OutlinedTextField(
               modifier = Modifier
                    .fillMaxWidth(),
               value = registerFormState.password1,
               onValueChange = {
                    viewModel.onEvent(RegisterListEvent.EnteredPassword1(it))
               },
               label = { Text(text = stringResource(R.string.password)) },
               singleLine = true,
               keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
               visualTransformation = if (registerFormState.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
               trailingIcon = {
                    val image =
                         if (registerFormState.passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (registerFormState.passwordVisibility)
                         stringResource(R.string.hide_password)
                    else stringResource(R.string.show_password)
                    IconButton(onClick = { viewModel.onEvent(RegisterListEvent.TogglePasswordVisibility) }) {
                         Icon(
                              imageVector = image,
                              contentDescription = description
                         )
                    }
               },
               isError = registerFormState.password1Error != null
          )
          if (registerFormState.password1Error != null) {
               Text(
                    text = registerFormState.password1Error.asString(),
                    color = MaterialTheme.colors.error
               )
          }

          OutlinedTextField(
               modifier = Modifier
                    .fillMaxWidth(),
               value = registerFormState.password2,
               onValueChange = {
                    viewModel.onEvent(RegisterListEvent.EnteredPassword2(it))
               },
               label = { Text(text = stringResource(R.string.password_2)) },
               singleLine = true,
               keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
               visualTransformation = if (registerFormState.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
               trailingIcon = {
                    val image =
                         if (registerFormState.passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (registerFormState.passwordVisibility)
                         stringResource(R.string.hide_password)
                    else stringResource(R.string.show_password)
                    IconButton(onClick = { viewModel.onEvent(RegisterListEvent.TogglePasswordVisibility) }) {
                         Icon(
                              imageVector = image,
                              contentDescription = description
                         )
                    }
               },
               isError = registerFormState.password2Error != null
          )
          if (registerFormState.password2Error != null) {
               Text(
                    text = registerFormState.password2Error.asString(),
                    color = MaterialTheme.colors.error
               )
          }

          Text(
               text = stringResource(id = R.string.password_requirement),
               color = TextInfoColor,
               modifier = Modifier
                    .fillMaxWidth()
          )

          Button(
               onClick = { viewModel.onEvent(RegisterListEvent.Register) },
               modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
          ) {
               Text(
                    text = stringResource(R.string.submit),
                    fontSize = 18.sp
               )
          }
     }
}