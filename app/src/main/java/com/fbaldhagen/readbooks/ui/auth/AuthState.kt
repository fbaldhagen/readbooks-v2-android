package com.fbaldhagen.readbooks.ui.auth

import com.fbaldhagen.readbooks.domain.usecase.AuthStatus

data class AuthState(
    val authStatus: AuthStatus = AuthStatus.LOADING,
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val passwordsMatch: Boolean = true,
    val registrationPending: Boolean = false,
    val error: String? = null
)