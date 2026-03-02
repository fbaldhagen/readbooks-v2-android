package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.AuthorDetails

interface AuthorRepository {
    suspend fun getAuthorDetails(
        authorName: String,
        excludeGutenbergId: Int?
    ): Result<AuthorDetails>
}