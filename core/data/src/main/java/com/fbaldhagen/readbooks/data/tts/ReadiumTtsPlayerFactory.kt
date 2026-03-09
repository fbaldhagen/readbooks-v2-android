package com.fbaldhagen.readbooks.data.tts

import android.app.Application
import android.content.Context
import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.domain.model.DomainLocator
import com.fbaldhagen.readbooks.domain.repository.TtsPlayer
import com.fbaldhagen.readbooks.domain.repository.TtsPlayerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import org.readium.navigator.media.tts.TtsNavigator
import org.readium.navigator.media.tts.TtsNavigatorFactory
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.mediatype.MediaType
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalReadiumApi::class)
@Singleton
class ReadiumTtsPlayerFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val publicationHolder: PublicationHolder
) : TtsPlayerFactory {

    override suspend fun create(
        bookId: Long,
        filePath: String?,
        startLocator: DomainLocator?
    ): Result<TtsPlayer> = suspendRunCatching {
        val publication = if (filePath != null) {
            publicationHolder.getOrOpen(bookId, filePath)
        } else {
            publicationHolder.get(bookId)
        } ?: throw IllegalStateException("No open publication for bookId $bookId")

        val application = context.applicationContext as Application

        val navigatorFactory = TtsNavigatorFactory.Companion(
            application = application,
            publication = publication
        ) ?: throw IllegalStateException("Publication does not support TTS")

        val readiumLocator = startLocator?.toReadium()

        val navigator = navigatorFactory.createNavigator(
            listener = object : TtsNavigator.Listener {
                override fun onStopRequested() {}
            },
            initialLocator = readiumLocator
        ).getOrElse {
            throw IllegalStateException("Failed to create TTS navigator: ${it.message}")
        }

        ReadiumTtsPlayer(navigator)
    }

    private fun DomainLocator.toReadium(): Locator = Locator(
        href = Url(href)!!,
        mediaType = MediaType.EPUB,
        locations = Locator.Locations(progression = progression)
    )
}