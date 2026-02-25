package com.fbaldhagen.readbooks.data.remote.api

import com.fbaldhagen.readbooks.data.remote.dto.AuthResponse
import com.fbaldhagen.readbooks.data.remote.dto.LoginRequest
import com.fbaldhagen.readbooks.data.remote.dto.RegisterRequest
import com.fbaldhagen.readbooks.data.remote.dto.RemoteBookRequest
import com.fbaldhagen.readbooks.data.remote.dto.RemoteBookResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReadBooksApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/books")
    suspend fun getBooks(): List<RemoteBookResponse>

    @POST("api/books")
    suspend fun saveBook(@Body request: RemoteBookRequest): RemoteBookResponse

    @PUT("api/books/{id}")
    suspend fun updateBook(
        @Path("id") id: Long,
        @Body request: RemoteBookRequest
    ): RemoteBookResponse

    @DELETE("api/books/{id}")
    suspend fun deleteBook(@Path("id") id: Long)

    companion object {
        const val BASE_URL = "https://api.fbaldhagen.dev/"
    }
}