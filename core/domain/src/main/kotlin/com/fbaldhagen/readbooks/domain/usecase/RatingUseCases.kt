package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.RemoteRating
import com.fbaldhagen.readbooks.domain.model.TopRatedBook
import com.fbaldhagen.readbooks.domain.repository.RatingRepository
import javax.inject.Inject

class RatingUseCases @Inject constructor(
    private val ratingRepository: RatingRepository
) {
    suspend fun getRatings(gutenbergId: Int): Result<RemoteRating> =
        ratingRepository.getRatings(gutenbergId)

    suspend fun submitRating(gutenbergId: Int, rating: Int): Result<Unit> =
        ratingRepository.submitRating(gutenbergId, rating)

    suspend fun deleteRating(gutenbergId: Int): Result<Unit> =
        ratingRepository.deleteRating(gutenbergId)

    suspend fun getTopRated(limit: Int, excludeIds: List<Int>): Result<List<TopRatedBook>> =
        ratingRepository.getTopRated(limit, excludeIds)
}