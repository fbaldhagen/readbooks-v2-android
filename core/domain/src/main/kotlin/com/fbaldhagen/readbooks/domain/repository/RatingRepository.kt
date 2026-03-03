package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.RemoteRating
import com.fbaldhagen.readbooks.domain.model.TopRatedBook

interface RatingRepository {
    suspend fun getRatings(gutenbergId: Int): Result<RemoteRating>
    suspend fun submitRating(gutenbergId: Int, rating: Int): Result<Unit>
    suspend fun deleteRating(gutenbergId: Int): Result<Unit>
    suspend fun getTopRated(limit: Int, excludeIds: List<Int>): Result<List<TopRatedBook>>
}