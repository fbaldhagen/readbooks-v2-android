package com.fbaldhagen.readbooks.domain.repository

import androidx.paging.PagingData
import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    fun search(query: String): Flow<PagingData<DiscoverBook>>
    fun getByTopic(topic: String): Flow<PagingData<DiscoverBook>>
    fun getPopular(): Flow<PagingData<DiscoverBook>>
    suspend fun getByTopicPreview(topic: String, limit: Int): Result<List<DiscoverBook>>
    suspend fun getPopularPreview(limit: Int): Result<List<DiscoverBook>>
    suspend fun getBookById(gutenbergId: Int): Result<DiscoverBook>
    suspend fun getBooksByAuthor(authorName: String, excludeId: Int): Result<List<DiscoverBook>>
}