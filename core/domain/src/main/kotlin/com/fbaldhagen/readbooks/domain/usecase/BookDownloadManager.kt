package com.fbaldhagen.readbooks.domain.usecase

import kotlinx.coroutines.flow.Flow

interface BookDownloadManager {
    fun enqueueDownload(gutenbergId: Int)
    fun observeDownloadState(gutenbergId: Int): Flow<DownloadState>
    fun cancelDownload(gutenbergId: Int)
}

sealed interface DownloadState {
    data object Idle : DownloadState
    data object Enqueued : DownloadState
    data object Running : DownloadState
    data class Succeeded(val bookId: Long) : DownloadState
    data class Failed(val message: String) : DownloadState
}