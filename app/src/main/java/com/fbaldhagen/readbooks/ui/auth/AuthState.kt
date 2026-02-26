package com.fbaldhagen.readbooks.ui.auth

data class AuthState(
    val isLoading: Boolean = false,
    val isCheckingAuth: Boolean = true,
    val isLoggedIn: Boolean = false,
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val error: String? = null,
    val registrationPending: Boolean = false,
    val confirmPassword: String = "",
    val passwordsMatch: Boolean = true
)