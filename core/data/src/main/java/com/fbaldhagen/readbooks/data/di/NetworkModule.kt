package com.fbaldhagen.readbooks.data.di

import com.fbaldhagen.readbooks.data.remote.dto.OpenLibraryBioAdapter
import com.fbaldhagen.readbooks.data.remote.AuthInterceptor
import com.fbaldhagen.readbooks.data.remote.api.GutendexApiService
import com.fbaldhagen.readbooks.data.remote.api.OpenLibraryApiService
import com.fbaldhagen.readbooks.data.remote.api.ReadBooksApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(OpenLibraryBioAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    @Provides
    @Singleton
    @GutendexRetrofit
    fun provideGutendexRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(GutendexApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideGutendexApiService(@GutendexRetrofit retrofit: Retrofit): GutendexApiService =
        retrofit.create(GutendexApiService::class.java)

    @Provides
    @Singleton
    @ReadBooksOkHttp
    fun provideReadBooksOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()

    @Provides
    @Singleton
    @ReadBooksRetrofit
    fun provideReadBooksRetrofit(
        @ReadBooksOkHttp okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(ReadBooksApiService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideReadBooksApiService(@ReadBooksRetrofit retrofit: Retrofit): ReadBooksApiService =
        retrofit.create(ReadBooksApiService::class.java)

    @Provides
    @Singleton
    @OpenLibraryRetrofit
    fun provideOpenLibraryRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(OpenLibraryApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideOpenLibraryApiService(
        @OpenLibraryRetrofit retrofit: Retrofit
    ): OpenLibraryApiService =
        retrofit.create(OpenLibraryApiService::class.java)
}