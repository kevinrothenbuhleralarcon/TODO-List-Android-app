package ch.kra.todo.auth.presentation.register

import androidx.lifecycle.ViewModel
import ch.kra.todo.auth.domain.use_case.Register
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val register: Register
): ViewModel() {

}