package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = authRepository.isLoggedIn()
}