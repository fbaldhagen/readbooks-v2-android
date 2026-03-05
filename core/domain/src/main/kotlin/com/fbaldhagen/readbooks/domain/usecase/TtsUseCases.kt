package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.DomainLocator
import com.fbaldhagen.readbooks.domain.model.TtsSettings
import com.fbaldhagen.readbooks.domain.repository.TtsPlayer
import com.fbaldhagen.readbooks.domain.repository.TtsPlayerFactory
import javax.inject.Inject

class TtsUseCases @Inject constructor(
    private val factory: TtsPlayerFactory
) {
    suspend fun createPlayer(
        bookId: Long,
        startLocator: DomainLocator? = null
    ): Result<TtsPlayer> = factory.create(bookId, startLocator)
}