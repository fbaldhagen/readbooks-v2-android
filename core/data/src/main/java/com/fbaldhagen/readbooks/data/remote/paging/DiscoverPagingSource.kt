package com.fbaldhagen.readbooks.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fbaldhagen.readbooks.data.remote.api.GutendexApiService
import com.fbaldhagen.readbooks.data.remote.dto.GutendexBookDto

class DiscoverPagingSource(
    private val apiService: GutendexApiService,
    private val search: String? = null,
    private val topic: String? = null,
    private val sort: String? = null
) : PagingSource<Int, GutendexBookDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GutendexBookDto> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getBooks(
                page = page,
                search = search,
                topic = topic,
                sort = sort
            )

            // Filter to only text items that have an EPUB format
            val books = response.results.filter { book ->
                book.mediaType == "Text" &&
                        book.formats.keys.any { it.contains("epub") }
            }

            LoadResult.Page(
                data = books,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.next == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GutendexBookDto>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}