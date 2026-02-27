package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

enum class AuthStatus {
    LOADING,
    AUTHENTICATED,
    GUEST,
    UNAUTHENTICATED
}

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthStatus> = combine(
        authRepository.isLoggedIn(),
        authRepository.isGuest()
    ) { isLoggedIn, isGuest ->
        when {
            isLoggedIn -> AuthStatus.AUTHENTICATED
            isGuest -> AuthStatus.GUEST
            else -> AuthStatus.UNAUTHENTICATED
        }
    }
}