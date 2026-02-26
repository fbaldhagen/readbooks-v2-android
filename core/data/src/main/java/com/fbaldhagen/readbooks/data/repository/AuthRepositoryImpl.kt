package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.data.remote.api.ReadBooksApiService
import com.fbaldhagen.readbooks.data.remote.dto.LoginRequest
import com.fbaldhagen.readbooks.data.remote.dto.RegisterRequest
import com.fbaldhagen.readbooks.domain.repository.AuthRepository
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ReadBooksApiService,
    private val userPreferencesRepository: UserPreferencesRepository
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        displayName: String?
    ): Result<Unit> {
        return try {
            apiService.register(RegisterRequest(email, password, displayName))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            userPreferencesRepository.saveAuthToken(response.token)
            userPreferencesRepository.saveUserInfo(email, 0L)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        userPreferencesRepository.clearAuthData()
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return userPreferencesRepository.observe().map { it.authToken != null }
    }

    override suspend fun verifyEmail(token: String): Result<Unit> {
        return try {
            val response = apiService.verifyEmail(token)
            userPreferencesRepository.saveAuthToken(response.token)
            userPreferencesRepository.saveUserInfo(response.email, 0L)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}