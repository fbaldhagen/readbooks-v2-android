package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.DomainLocator

interface TtsPlayerFactory {
    suspend fun create(
        bookId: Long,
        filePath: String? = null,
        startLocator: DomainLocator? = null
    ): Result<TtsPlayer>
}