package com.fbaldhagen.readbooks.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GutendexRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ReadBooksRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ReadBooksOkHttp