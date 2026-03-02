package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.AuthorDetails
import com.fbaldhagen.readbooks.domain.repository.AuthorRepository
import javax.inject.Inject

class GetAuthorDetailsUseCase @Inject constructor(
    private val authorRepository: AuthorRepository
) {
    suspend operator fun invoke(
        authorName: String,
        excludeGutenbergId: Int?
    ): Result<AuthorDetails> =
        authorRepository.getAuthorDetails(authorName, excludeGutenbergId)
}