package com.fbaldhagen.readbooks.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fbaldhagen.readbooks.data.local.db.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY added_at DESC")
    fun observeAll(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun observeById(bookId: Long): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE reading_status = :status AND is_archived = 0 " +
            "ORDER BY last_read_at DESC")
    fun observeByStatus(status: String): Flow<List<BookEntity>>

    @Query(
        "SELECT * FROM books WHERE is_archived = 0 " +
                "ORDER BY last_read_at DESC LIMIT :limit"
    )
    fun observeRecent(limit: Int): Flow<List<BookEntity>>

    @Query("SELECT COUNT(*) FROM books WHERE is_archived = 0")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE reading_status = 'FINISHED'")
    fun observeFinishedCount(): Flow<Int>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getById(bookId: Long): BookEntity?

    @Query("SELECT * FROM books WHERE gutenberg_id = :gutenbergId")
    suspend fun getByGutenbergId(gutenbergId: Int): BookEntity?

    @Query(
        """
        SELECT * FROM books 
        WHERE (:showArchived = 0 OR is_archived = 1)
        AND (:searchQuery = '' OR title LIKE '%' || :searchQuery || '%' OR authors LIKE '%' || :searchQuery || '%')
        AND (:readingStatus IS NULL OR reading_status = :readingStatus)
        ORDER BY 
            CASE WHEN :sortType = 'TITLE' THEN title END ASC,
            CASE WHEN :sortType = 'AUTHOR' THEN json_extract(authors, '$[0]') END ASC,
            CASE WHEN :sortType = 'RECENTLY_ADDED' THEN added_at END DESC,
            CASE WHEN :sortType = 'LAST_READ' THEN last_read_at END DESC
        """
    )
    fun observeFiltered(
        searchQuery: String,
        readingStatus: String?,
        sortType: String,
        showArchived: Boolean
    ): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity): Long

    @Update
    suspend fun update(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun delete(bookId: Long)

    @Query("UPDATE books SET reading_status = :status WHERE id = :bookId")
    suspend fun updateReadingStatus(bookId: Long, status: String)

    @Query(
        "UPDATE books SET current_locator = :locator, progress = :progress, " +
                "last_read_at = :timestamp WHERE id = :bookId"
    )
    suspend fun updateProgress(bookId: Long, locator: String, progress: Float, timestamp: Long)

    @Query("UPDATE books SET last_read_at = :timestamp WHERE id = :bookId")
    suspend fun updateLastReadAt(bookId: Long, timestamp: Long)

    @Query("UPDATE books SET rating = :rating WHERE id = :bookId")
    suspend fun updateRating(bookId: Long, rating: Int?)

    @Query("UPDATE books SET is_archived = NOT is_archived WHERE id = :bookId")
    suspend fun toggleArchived(bookId: Long)

    @Query(
        "UPDATE books SET reading_status = 'NOT_STARTED', progress = 0, " +
                "current_locator = NULL, last_read_at = NULL WHERE id = :bookId"
    )
    suspend fun resetProgress(bookId: Long)

    @Query("UPDATE books SET is_archived = 1, file_path = NULL WHERE id = :bookId")
    suspend fun archiveBook(bookId: Long)
}