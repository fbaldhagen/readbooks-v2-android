package com.fbaldhagen.readbooks.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.usecase.LoginUseCase
import com.fbaldhagen.readbooks.domain.usecase.ObserveAuthStateUseCase
import com.fbaldhagen.readbooks.domain.usecase.RegisterUseCase
import com.fbaldhagen.readbooks.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val observeAuthState: ObserveAuthStateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        observeLoginState()
    }

    private fun observeLoginState() {
        viewModelScope.launch {
            observeAuthState().collect { isLoggedIn ->
                _state.value = _state.value.copy(
                    isLoggedIn = isLoggedIn,
                    isCheckingAuth = false
                )
            }
        }
    }

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onDisplayNameChange(displayName: String) {
        _state.value = _state.value.copy(displayName = displayName)
    }

    fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = loginUseCase(_state.value.email, _state.value.password)
            _state.value = _state.value.copy(isLoading = false)
            result.onFailure { e ->
                _state.value = _state.value.copy(error = e.message ?: "Login failed")
            }
        }
    }

    fun register() {
        if (_state.value.password != _state.value.confirmPassword) {
            _state.value = _state.value.copy(passwordsMatch = false)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = registerUseCase(
                _state.value.email,
                _state.value.password,
                _state.value.displayName.ifBlank { null }
            )
            _state.value = _state.value.copy(isLoading = false)
            result.onSuccess {
                _state.value = _state.value.copy(registrationPending = true)
            }.onFailure { e ->
                _state.value = _state.value.copy(error = e.message ?: "Registration failed")
            }
        }
    }

    fun verifyEmail(token: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = verifyEmailUseCase(token)
            result.onSuccess {
                _state.value = _state.value.copy(isLoading = false)
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Verification failed"
                )
            }
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }

    fun dismissRegistrationPending() {
        _state.value = _state.value.copy(registrationPending = false)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.value = _state.value.copy(
            confirmPassword = confirmPassword,
            passwordsMatch = _state.value.password == confirmPassword
        )
    }

    fun clearPasswordConfirmation() {
        _state.value = _state.value.copy(
            confirmPassword = "",
            passwordsMatch = true
        )
    }
}