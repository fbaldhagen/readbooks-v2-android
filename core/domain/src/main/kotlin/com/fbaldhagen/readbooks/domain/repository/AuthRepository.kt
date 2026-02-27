package com.fbaldhagen.readbooks.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun continueAsGuest()
    suspend fun register(email: String, password: String, displayName: String?): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
    fun isLoggedIn(): Flow<Boolean>
    fun isGuest(): Flow<Boolean>
    suspend fun verifyEmail(token: String): Result<Unit>
}