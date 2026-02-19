package com.fbaldhagen.readbooks.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fbaldhagen.readbooks.data.local.db.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks WHERE book_id = :bookId ORDER BY created_at DESC")
    fun observeForBook(bookId: Long): Flow<List<BookmarkEntity>>

    @Insert
    suspend fun insert(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun delete(bookmarkId: Long)

    @Update
    suspend fun update(bookmark: BookmarkEntity)
}