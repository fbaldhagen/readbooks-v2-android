package com.fbaldhagen.readbooks.domain.repository

interface ConnectivityRepository {
    suspend fun pingBackend(): Boolean
}