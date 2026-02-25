package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}