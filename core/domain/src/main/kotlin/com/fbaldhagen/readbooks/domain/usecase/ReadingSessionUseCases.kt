package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.ReadingSession
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadingSessionUseCases @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    fun observeForBook(bookId: Long): Flow<List<ReadingSession>> =
        sessionRepository.observeForBook(bookId)

    fun observeAll(): Flow<List<ReadingSession>> =
        sessionRepository.observeAll()

    suspend fun getSessionsInRange(startTime: Long, endTime: Long): Result<List<ReadingSession>> =
        sessionRepository.getSessionsInRange(startTime, endTime)

    suspend fun getTodayMinutes(): Result<Int> =
        sessionRepository.getTodayMinutes()

    suspend fun start(bookId: Long): Result<Long> =
        sessionRepository.start(bookId)

    suspend fun end(sessionId: Long, pagesRead: Int = 0): Result<Unit> =
        sessionRepository.end(sessionId, pagesRead)
}