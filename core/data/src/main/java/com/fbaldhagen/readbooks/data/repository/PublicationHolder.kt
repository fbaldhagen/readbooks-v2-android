package com.fbaldhagen.readbooks.data.repository

import org.readium.r2.shared.publication.Publication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicationHolder @Inject constructor() {

    private var bookId: Long? = null
    private var publication: Publication? = null

    fun set(bookId: Long, publication: Publication) {
        this.bookId = bookId
        this.publication = publication
    }

    fun get(bookId: Long): Publication? =
        if (this.bookId == bookId) publication else null

    fun clear() {
        bookId = null
        publication = null
    }
}