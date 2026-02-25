package com.fbaldhagen.readbooks.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.repository.AuthRepository
import com.fbaldhagen.readbooks.domain.usecase.LoginUseCase
import com.fbaldhagen.readbooks.domain.usecase.RegisterUseCase
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect { isLoggedIn ->
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
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = registerUseCase(
                _state.value.email,
                _state.value.password,
                _state.value.displayName.ifBlank { null }
            )
            _state.value = _state.value.copy(isLoading = false)
            result.onFailure { e ->
                _state.value = _state.value.copy(error = e.message ?: "Registration failed")
            }
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}