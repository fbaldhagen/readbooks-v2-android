package com.fbaldhagen.readbooks.data.repository

import androidx.core.net.toUri
import com.fbaldhagen.readbooks.data.local.file.UserFileManager
import com.fbaldhagen.readbooks.domain.repository.UserStorageRepository
import javax.inject.Inject

class UserStorageRepositoryImpl @Inject constructor(
    private val userFileManager: UserFileManager
) : UserStorageRepository {

    override suspend fun saveAvatar(uri: String): String {
        return userFileManager.saveAvatar(uri.toUri()).absolutePath
    }

    override suspend fun deleteAvatar() {
        userFileManager.deleteAvatar()
    }
}