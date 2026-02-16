package com.fbaldhagen.readbooks.data.remote.api

import com.fbaldhagen.readbooks.data.remote.dto.GutendexBookDto
import com.fbaldhagen.readbooks.data.remote.dto.GutendexResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GutendexApiService {

    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null,
        @Query("topic") topic: String? = null,
        @Query("sort") sort: String? = null,
        @Query("languages") languages: String? = null
    ): GutendexResponseDto

    @GET("books/{id}")
    suspend fun getBookById(
        @Path("id") id: Int
    ): GutendexBookDto

    companion object {
        const val BASE_URL = "https://gutendex.com/"
    }
}