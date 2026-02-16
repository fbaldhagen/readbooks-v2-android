package com.fbaldhagen.readbooks.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.fbaldhagen.readbooks.data.local.db.dao.BookDao
import com.fbaldhagen.readbooks.data.local.db.entity.BookEntity
import com.fbaldhagen.readbooks.data.local.file.BookFileManager
import com.fbaldhagen.readbooks.data.remote.api.GutendexApiService
import com.fbaldhagen.readbooks.data.remote.dto.toDiscoverBook
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request

@HiltWorker
class DownloadBookWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val apiService: GutendexApiService,
    private val bookDao: BookDao,
    private val bookFileManager: BookFileManager,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val gutenbergId = inputData.getInt(KEY_GUTENBERG_ID, -1)
        if (gutenbergId == -1) return Result.failure()

        return try {
            // Fetch book metadata
            val dto = apiService.getBookById(gutenbergId)
            val discoverBook = dto.toDiscoverBook()

            val downloadUrl = discoverBook.downloadUrl
                ?: return Result.failure(
                    workDataOf(KEY_ERROR to "No EPUB download URL available")
                )

            // Download EPUB
            val epubRequest = Request.Builder().url(downloadUrl).build()
            val epubResponse = okHttpClient.newCall(epubRequest).execute()
            if (!epubResponse.isSuccessful) {
                return Result.failure(
                    workDataOf(KEY_ERROR to "Download failed: ${epubResponse.code}")
                )
            }

            val epubFile = epubResponse.body.byteStream().use { stream ->
                bookFileManager.saveEpub(gutenbergId, stream)
            }

            // Download cover
            var coverPath: String? = null
            discoverBook.coverUrl?.let { coverUrl ->
                try {
                    val coverRequest = Request.Builder().url(coverUrl).build()
                    val coverResponse = okHttpClient.newCall(coverRequest).execute()
                    if (coverResponse.isSuccessful) {
                        coverResponse.body.byteStream().use { stream ->
                            val coverFile = bookFileManager.saveCover(gutenbergId, stream)
                            coverPath = coverFile.absolutePath
                        }
                    }
                } catch (_: Exception) {
                    // Cover download failure is non-fatal
                }
            }

            // Save to database
            val authors = Moshi.Builder().build()
                .adapter<List<String>>(
                    Types.newParameterizedType(
                        List::class.java, String::class.java
                    )
                )
                .toJson(discoverBook.authors)

            val subjects = Moshi.Builder().build()
                .adapter<List<String>>(
                    Types.newParameterizedType(
                        List::class.java, String::class.java
                    )
                )
                .toJson(discoverBook.subjects)

            val bookId = bookDao.insert(
                BookEntity(
                    title = discoverBook.title,
                    authors = authors,
                    description = discoverBook.summary,
                    coverUri = coverPath,
                    filePath = epubFile.absolutePath,
                    gutenbergId = gutenbergId,
                    subjects = subjects
                )
            )

            Result.success(workDataOf(KEY_BOOK_ID to bookId))
        } catch (e: Exception) {
            if (runAttemptCount < 2) {
                Result.retry()
            } else {
                Result.failure(workDataOf(KEY_ERROR to (e.message ?: "Unknown error")))
            }
        }
    }

    companion object {
        const val KEY_GUTENBERG_ID = "gutenberg_id"
        const val KEY_BOOK_ID = "book_id"
        const val KEY_ERROR = "error"
        const val WORK_NAME_PREFIX = "download_book_"
    }
}