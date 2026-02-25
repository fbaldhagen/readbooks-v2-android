package com.fbaldhagen.readbooks.domain.repository

interface UserStorageRepository {
    suspend fun saveAvatar(uri: String): String
    suspend fun deleteAvatar()
}