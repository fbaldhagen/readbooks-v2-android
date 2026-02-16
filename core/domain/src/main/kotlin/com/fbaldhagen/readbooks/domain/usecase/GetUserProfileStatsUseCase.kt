package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.getOrNull
import com.fbaldhagen.readbooks.domain.model.UserProfileStats
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetUserProfileStatsUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<UserProfileStats> = combine(
        bookRepository.observeTotalCount(),
        bookRepository.observeFinishedCount(),
        sessionRepository.observeAll()
    ) { total, finished, sessions ->
        val totalMinutes = sessions.sumOf { it.durationMinutes }

        UserProfileStats(
            totalBooks = total,
            finishedBooks = finished,
            totalReadingMinutes = totalMinutes,
            currentStreak = 0 // Simplified — full streak calc is in analytics
        )
    }
}