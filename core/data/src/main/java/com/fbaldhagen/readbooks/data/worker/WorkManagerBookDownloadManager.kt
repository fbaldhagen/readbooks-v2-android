package com.fbaldhagen.readbooks.data.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.fbaldhagen.readbooks.domain.usecase.BookDownloadManager
import com.fbaldhagen.readbooks.domain.usecase.DownloadState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkManagerBookDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) : BookDownloadManager {

    private val workManager = WorkManager.getInstance(context)

    override fun enqueueDownload(gutenbergId: Int) {
        val request = OneTimeWorkRequestBuilder<DownloadBookWorker>()
            .setInputData(
                workDataOf(DownloadBookWorker.KEY_GUTENBERG_ID to gutenbergId)
            )
            .build()

        workManager.enqueueUniqueWork(
            "${DownloadBookWorker.WORK_NAME_PREFIX}$gutenbergId",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    override fun observeDownloadState(gutenbergId: Int): Flow<DownloadState> {
        val workName = "${DownloadBookWorker.WORK_NAME_PREFIX}$gutenbergId"
        return workManager.getWorkInfosForUniqueWorkFlow(workName)
            .map { workInfos ->
                val workInfo = workInfos.firstOrNull()
                when (workInfo?.state) {
                    WorkInfo.State.ENQUEUED -> DownloadState.Enqueued
                    WorkInfo.State.RUNNING -> DownloadState.Running
                    WorkInfo.State.SUCCEEDED -> {
                        val bookId = workInfo.outputData.getLong(
                            DownloadBookWorker.KEY_BOOK_ID, -1
                        )
                        DownloadState.Succeeded(bookId)
                    }
                    WorkInfo.State.FAILED -> {
                        val error = workInfo.outputData.getString(
                            DownloadBookWorker.KEY_ERROR
                        ) ?: "Download failed"
                        DownloadState.Failed(error)
                    }
                    WorkInfo.State.CANCELLED -> DownloadState.Failed("Download cancelled")
                    WorkInfo.State.BLOCKED, null -> DownloadState.Idle
                }
            }
    }

    override fun cancelDownload(gutenbergId: Int) {
        workManager.cancelUniqueWork(
            "${DownloadBookWorker.WORK_NAME_PREFIX}$gutenbergId"
        )
    }
}