package com.fbaldhagen.readbooks.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null
)

data class RegisterResponse(val message: String)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val email: String,
    val displayName: String?
)