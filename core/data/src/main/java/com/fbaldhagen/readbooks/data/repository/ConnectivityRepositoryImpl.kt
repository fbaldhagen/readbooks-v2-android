package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.data.remote.api.ReadBooksApiService
import com.fbaldhagen.readbooks.domain.repository.ConnectivityRepository
import javax.inject.Inject

class ConnectivityRepositoryImpl @Inject constructor(
    private val apiService: ReadBooksApiService
) : ConnectivityRepository {
    override suspend fun pingBackend(): Boolean {
        return try {
            apiService.health()
            true
        } catch (_: Exception) {
            false
        }
    }
}