package com.fbaldhagen.readbooks.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.data.remote.api.GutendexApiService
import com.fbaldhagen.readbooks.data.remote.dto.toDiscoverBook
import com.fbaldhagen.readbooks.data.remote.paging.DiscoverPagingSource
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiscoverRepositoryImpl @Inject constructor(
    private val apiService: GutendexApiService
) : DiscoverRepository {

    override fun search(query: String): Flow<PagingData<DiscoverBook>> = createPager(
        search = query
    )

    override fun getByTopic(topic: String): Flow<PagingData<DiscoverBook>> = createPager(
        topic = topic
    )

    override fun getPopular(): Flow<PagingData<DiscoverBook>> = createPager(
        sort = "popular"
    )

    override suspend fun getBookById(gutenbergId: Int): Result<DiscoverBook> =
        suspendRunCatching {
            apiService.getBookById(gutenbergId).toDiscoverBook()
        }

    override suspend fun getBooksByAuthor(
        authorName: String,
        excludeId: Int
    ): Result<List<DiscoverBook>> = suspendRunCatching {
        apiService.getBooks(search = authorName)
            .results
            .filter { dto ->
                dto.id != excludeId &&
                        dto.authors.any { it.name == authorName }
            }
            .map { it.toDiscoverBook() }
    }

    private fun createPager(
        search: String? = null,
        topic: String? = null,
        sort: String? = null
    ): Flow<PagingData<DiscoverBook>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            DiscoverPagingSource(
                apiService = apiService,
                search = search,
                topic = topic,
                sort = sort
            )
        }
    ).flow.map { pagingData ->
        pagingData.map { dto -> dto.toDiscoverBook() }
    }

    companion object {
        private const val PAGE_SIZE = 32
        private const val PREFETCH_DISTANCE = 8
    }
}