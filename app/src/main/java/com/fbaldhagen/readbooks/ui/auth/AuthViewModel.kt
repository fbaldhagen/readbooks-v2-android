package com.fbaldhagen.readbooks.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.usecase.ContinueAsGuestUseCase
import com.fbaldhagen.readbooks.domain.usecase.LoginUseCase
import com.fbaldhagen.readbooks.domain.usecase.ObserveAuthStateUseCase
import com.fbaldhagen.readbooks.domain.usecase.RegisterUseCase
import com.fbaldhagen.readbooks.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val observeAuthState: ObserveAuthStateUseCase,
    private val continueAsGuest: ContinueAsGuestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        observeLoginState()
    }

    private fun observeLoginState() {
        viewModelScope.launch {
            observeAuthState().collect { status ->
                _state.update { it.copy(authStatus = status) }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase(_state.value.email, _state.value.password)
            _state.update { it.copy(isLoading = false) }
            result.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Login failed") }
            }
        }
    }

    fun register() {
        if (_state.value.password != _state.value.confirmPassword) {
            _state.update { it.copy(passwordsMatch = false) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = registerUseCase(
                _state.value.email,
                _state.value.password,
                _state.value.displayName.ifBlank { null }
            )
            _state.update { it.copy(isLoading = false) }
            result.onSuccess {
                _state.update { it.copy(registrationPending = true) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Registration failed") }
            }
        }
    }

    fun verifyEmail(token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = verifyEmailUseCase(token)
            _state.update { it.copy(isLoading = false) }
            result.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Verification failed") }
            }
        }
    }

    fun onContinueAsGuest() {
        viewModelScope.launch { continueAsGuest() }
    }

    fun onEmailChange(email: String) { _state.update { it.copy(email = email) } }
    fun onPasswordChange(password: String) { _state.update { it.copy(password = password) } }
    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(
            confirmPassword = confirmPassword,
            passwordsMatch = _state.value.password == confirmPassword
        )}
    }

    fun dismissError() { _state.update { it.copy(error = null) } }
    fun dismissRegistrationPending() { _state.update { it.copy(registrationPending = false) } }
    fun clearPasswordConfirmation() { _state.update { it.copy(confirmPassword = "", passwordsMatch = true) } }
}