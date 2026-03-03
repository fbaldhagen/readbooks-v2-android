package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.data.remote.api.ReadBooksApiService
import com.fbaldhagen.readbooks.data.remote.dto.RatingRequest
import com.fbaldhagen.readbooks.data.remote.dto.toRemoteRating
import com.fbaldhagen.readbooks.data.remote.dto.toTopRatedBook
import com.fbaldhagen.readbooks.domain.model.RemoteRating
import com.fbaldhagen.readbooks.domain.model.TopRatedBook
import com.fbaldhagen.readbooks.domain.repository.RatingRepository
import javax.inject.Inject

class RatingRepositoryImpl @Inject constructor(
    private val apiService: ReadBooksApiService
) : RatingRepository {

    override suspend fun getRatings(gutenbergId: Int): Result<RemoteRating> =
        suspendRunCatching {
            apiService.getRatings(gutenbergId).toRemoteRating()
        }

    override suspend fun submitRating(gutenbergId: Int, rating: Int): Result<Unit> =
        suspendRunCatching {
            apiService.submitRating(gutenbergId, RatingRequest(rating))
        }

    override suspend fun deleteRating(gutenbergId: Int): Result<Unit> =
        suspendRunCatching {
            apiService.deleteRating(gutenbergId)
        }

    override suspend fun getTopRated(
        limit: Int,
        excludeIds: List<Int>
    ): Result<List<TopRatedBook>> = suspendRunCatching {
        apiService.getTopRated(limit, excludeIds).map { it.toTopRatedBook() }
    }
}