package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.data.local.db.dao.ReadingSessionDao
import com.fbaldhagen.readbooks.data.local.db.entity.ReadingSessionEntity
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.domain.model.ReadingSession
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: ReadingSessionDao
) : SessionRepository {

    override fun observeForBook(bookId: Long): Flow<List<ReadingSession>> =
        sessionDao.observeForBook(bookId).map { entities -> entities.map { it.toDomain() } }

    override fun observeAll(): Flow<List<ReadingSession>> =
        sessionDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getSessionsInRange(
        startTime: Long,
        endTime: Long
    ): Result<List<ReadingSession>> = suspendRunCatching {
        sessionDao.getSessionsInRange(startTime, endTime).map { it.toDomain() }
    }

    override suspend fun getTodayMinutes(): Result<Int> = suspendRunCatching {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        sessionDao.getMinutesSince(todayStart)
    }

    override suspend fun start(bookId: Long, gutenbergId: Int?): Result<Long> = suspendRunCatching {
        sessionDao.insert(
            ReadingSessionEntity(
                bookId = bookId,
                gutenbergId = gutenbergId,
                startTime = System.currentTimeMillis()
            )
        )
    }

    override suspend fun end(sessionId: Long, pagesRead: Int): Result<Unit> = suspendRunCatching {
        val session = sessionDao.getById(sessionId)
            ?: throw NoSuchElementException("Session $sessionId not found")

        val now = System.currentTimeMillis()
        val durationMinutes = ((now - session.startTime) / 60_000).toInt()

        sessionDao.endSession(
            sessionId = sessionId,
            endTime = now,
            durationMinutes = durationMinutes,
            pagesRead = pagesRead
        )
    }

    override suspend fun getForBook(bookId: Long): Result<List<ReadingSession>> =
        suspendRunCatching {
            sessionDao.getForBook(bookId).map { it.toDomain() }
        }

    override suspend fun getAllSessions(): Result<List<ReadingSession>> = suspendRunCatching {
        sessionDao.getAll().map { it.toDomain() }
    }

    override fun observeTodayMinutes(): Flow<Int> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return sessionDao.observeMinutesSince(todayStart)
    }
}