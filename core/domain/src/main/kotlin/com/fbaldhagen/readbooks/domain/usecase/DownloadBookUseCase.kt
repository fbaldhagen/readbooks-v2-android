package com.fbaldhagen.readbooks.domain.usecase

import javax.inject.Inject

class DownloadBookUseCase @Inject constructor(
    private val downloadManager: BookDownloadManager
) {
    fun execute(gutenbergId: Int) {
        downloadManager.enqueueDownload(gutenbergId)
    }

    fun observeDownloadState(gutenbergId: Int) =
        downloadManager.observeDownloadState(gutenbergId)
}