package com.fbaldhagen.readbooks.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.fbaldhagen.readbooks.data.local.db.entity.BookCollectionCrossRef
import com.fbaldhagen.readbooks.data.local.db.entity.CollectionEntity
import com.fbaldhagen.readbooks.data.local.db.entity.CollectionWithBooksRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query("SELECT * FROM collections ORDER BY created_at DESC")
    fun observeAll(): Flow<List<CollectionEntity>>

    @Transaction
    @Query("SELECT * FROM collections ORDER BY created_at DESC")
    fun observeAllWithBooks(): Flow<List<CollectionWithBooksRelation>>

    @Transaction
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun observeWithBooks(collectionId: Long): Flow<CollectionWithBooksRelation?>

    @Query(
        """
        SELECT c.* FROM collections c
        INNER JOIN book_collection_cross_ref ref ON c.id = ref.collection_id
        WHERE ref.book_id = :bookId
        ORDER BY c.created_at DESC
        """
    )
    fun observeCollectionsForBook(bookId: Long): Flow<List<CollectionEntity>>

    @Insert
    suspend fun insert(collection: CollectionEntity): Long

    @Query("UPDATE collections SET name = :name WHERE id = :collectionId")
    suspend fun rename(collectionId: Long, name: String)

    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun delete(collectionId: Long)

    @Insert
    suspend fun addBookToCollection(crossRef: BookCollectionCrossRef)

    @Query(
        "DELETE FROM book_collection_cross_ref " +
                "WHERE book_id = :bookId AND collection_id = :collectionId"
    )
    suspend fun removeBookFromCollection(collectionId: Long, bookId: Long)
}