package com.fbaldhagen.readbooks.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fbaldhagen.readbooks.data.local.db.entity.ReadingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {

    @Query("SELECT * FROM reading_sessions WHERE book_id = :bookId ORDER BY start_time DESC")
    fun observeForBook(bookId: Long): Flow<List<ReadingSessionEntity>>

    @Query("SELECT * FROM reading_sessions ORDER BY start_time DESC")
    fun observeAll(): Flow<List<ReadingSessionEntity>>

    @Query(
        "SELECT * FROM reading_sessions " +
                "WHERE start_time >= :startTime AND start_time <= :endTime"
    )
    suspend fun getSessionsInRange(startTime: Long, endTime: Long): List<ReadingSessionEntity>

    @Query(
        "SELECT COALESCE(SUM(duration_minutes), 0) FROM reading_sessions " +
                "WHERE start_time >= :dayStart"
    )
    suspend fun getMinutesSince(dayStart: Long): Int

    @Insert
    suspend fun insert(session: ReadingSessionEntity): Long

    @Query(
        "UPDATE reading_sessions SET end_time = :endTime, " +
                "duration_minutes = :durationMinutes, pages_read = :pagesRead " +
                "WHERE id = :sessionId"
    )
    suspend fun endSession(sessionId: Long, endTime: Long, durationMinutes: Int, pagesRead: Int)

    @Query("SELECT * FROM reading_sessions WHERE id = :sessionId")
    suspend fun getById(sessionId: Long): ReadingSessionEntity?

    @Query("SELECT * FROM reading_sessions WHERE book_id = :bookId ORDER BY start_time DESC")
    suspend fun getForBook(bookId: Long): List<ReadingSessionEntity>

    @Query("SELECT * FROM reading_sessions")
    suspend fun getAll(): List<ReadingSessionEntity>

    @Query(
        "SELECT COALESCE(SUM(duration_minutes), 0) FROM reading_sessions " +
                "WHERE start_time >= :dayStart"
    )
    fun observeMinutesSince(dayStart: Long): Flow<Int>
}