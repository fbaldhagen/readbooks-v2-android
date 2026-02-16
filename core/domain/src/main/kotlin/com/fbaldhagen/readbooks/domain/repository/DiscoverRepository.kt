package com.fbaldhagen.readbooks.domain.repository

import androidx.paging.PagingData
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {

    fun search(query: String): Flow<PagingData<DiscoverBook>>

    fun getByTopic(topic: String): Flow<PagingData<DiscoverBook>>

    fun getPopular(): Flow<PagingData<DiscoverBook>>
}