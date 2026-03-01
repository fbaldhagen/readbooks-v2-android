package com.fbaldhagen.readbooks.data.local.file

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class BookFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val booksDir: File
        get() = File(context.filesDir, "books").also { it.mkdirs() }

    private val coversDir: File
        get() = File(context.filesDir, "covers").also { it.mkdirs() }

    fun getEpubFile(gutenbergId: Int): File =
        File(booksDir, "$gutenbergId.epub")

    fun getCoverFile(gutenbergId: Int): File =
        File(coversDir, "$gutenbergId.jpg")

    fun saveEpub(gutenbergId: Int, inputStream: InputStream): File {
        val file = getEpubFile(gutenbergId)
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    fun saveCover(gutenbergId: Int, inputStream: InputStream): File {
        val file = getCoverFile(gutenbergId)
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    fun deleteBookFiles(gutenbergId: Int) {
        getEpubFile(gutenbergId).delete()
        getCoverFile(gutenbergId).delete()
    }

    fun archiveBook(gutenbergId: Int) {
        getEpubFile(gutenbergId).delete()
    }

    fun epubExists(gutenbergId: Int): Boolean =
        getEpubFile(gutenbergId).exists()
}