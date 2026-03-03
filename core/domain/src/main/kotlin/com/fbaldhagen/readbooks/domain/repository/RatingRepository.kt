package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.RemoteRating

interface RatingRepository {
    suspend fun getRatings(gutenbergId: Int): Result<RemoteRating>
    suspend fun submitRating(gutenbergId: Int, rating: Int): Result<Unit>
    suspend fun deleteRating(gutenbergId: Int): Result<Unit>
}