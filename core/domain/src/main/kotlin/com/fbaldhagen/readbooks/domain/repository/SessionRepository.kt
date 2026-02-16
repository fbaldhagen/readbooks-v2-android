package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.ReadingSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    fun observeForBook(bookId: Long): Flow<List<ReadingSession>>

    fun observeAll(): Flow<List<ReadingSession>>

    suspend fun getSessionsInRange(startTime: Long, endTime: Long): Result<List<ReadingSession>>

    suspend fun getTodayMinutes(): Result<Int>

    suspend fun start(bookId: Long): Result<Long>

    suspend fun end(sessionId: Long, pagesRead: Int = 0): Result<Unit>
}