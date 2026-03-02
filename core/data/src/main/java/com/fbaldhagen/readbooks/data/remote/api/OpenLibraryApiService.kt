package com.fbaldhagen.readbooks.data.remote.api

import com.fbaldhagen.readbooks.data.remote.dto.OpenLibraryAuthorDto
import com.fbaldhagen.readbooks.data.remote.dto.OpenLibraryAuthorSearchDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApiService {

    @GET("search/authors.json")
    suspend fun searchAuthor(
        @Query("q") name: String
    ): OpenLibraryAuthorSearchDto

    @GET("authors/{id}.json")
    suspend fun getAuthor(
        @Path("id") id: String
    ): OpenLibraryAuthorDto

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
    }
}