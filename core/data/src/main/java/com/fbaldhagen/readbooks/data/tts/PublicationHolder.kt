package com.fbaldhagen.readbooks.data.tts

import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.streamer.PublicationOpener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicationHolder @Inject constructor(
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener
) {

    private var bookId: Long? = null
    private var publication: Publication? = null

    fun set(bookId: Long, publication: Publication) {
        this.bookId = bookId
        this.publication = publication
    }

    fun get(bookId: Long): Publication? =
        if (this.bookId == bookId) publication else null

    suspend fun getOrOpen(bookId: Long, filePath: String): Publication? {
        get(bookId)?.let { return it }

        val url = AbsoluteUrl("file://$filePath") ?: return null

        val asset = assetRetriever.retrieve(url)
            .getOrElse { return null }

        val publication = publicationOpener.open(
            asset = asset,
            allowUserInteraction = false
        ).getOrElse { return null }

        set(bookId, publication)
        return publication
    }

    fun clear() {
        bookId = null
        publication = null
    }
}