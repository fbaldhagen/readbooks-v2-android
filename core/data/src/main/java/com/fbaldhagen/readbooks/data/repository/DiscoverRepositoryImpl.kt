package com.fbaldhagen.readbooks.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
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