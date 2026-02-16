package com.fbaldhagen.readbooks.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val authors: String, // JSON array stored as string
    val description: String? = null,
    @ColumnInfo(name = "cover_uri")
    val coverUri: String? = null,
    @ColumnInfo(name = "file_path")
    val filePath: String? = null,
    @ColumnInfo(name = "gutenberg_id")
    val gutenbergId: Int? = null,
    val language: String? = null,
    val subjects: String = "[]", // JSON array stored as string
    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_read_at")
    val lastReadAt: Long? = null,
    @ColumnInfo(name = "reading_status")
    val readingStatus: String = "NOT_STARTED",
    @ColumnInfo(name = "current_locator")
    val currentLocator: String? = null,
    val progress: Float = 0f,
    val rating: Int? = null,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false
)