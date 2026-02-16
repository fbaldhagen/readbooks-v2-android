package com.fbaldhagen.readbooks.domain.usecase

import androidx.paging.PagingData
import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiscoverUseCases @Inject constructor(
    private val discoverRepository: DiscoverRepository
) {
    fun search(query: String): Flow<PagingData<DiscoverBook>> =
        discoverRepository.search(query)

    fun getByTopic(topic: String): Flow<PagingData<DiscoverBook>> =
        discoverRepository.getByTopic(topic)

    fun getPopular(): Flow<PagingData<DiscoverBook>> =
        discoverRepository.getPopular()

    suspend fun getBookById(gutenbergId: Int): Result<DiscoverBook> =
        discoverRepository.getBookById(gutenbergId)
}