package com.fbaldhagen.readbooks.data.di

import com.fbaldhagen.readbooks.data.remote.api.GutendexApiService
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BaseUrlInterceptor @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Only rewrite requests targeting a Gutendex host
        val host = original.url.host
        val isGutendexRequest = host == GutendexApiService.BASE_URL
            .removePrefix("https://").trimEnd('/')
                || host == GutendexApiService.PUBLIC_URL
            .removePrefix("https://").trimEnd('/')

        if (!isGutendexRequest) return chain.proceed(original)

        val usePublic = runBlocking {
            userPreferencesRepository.observe().first().usePublicGutenberg
        }
        val baseUrl = if (usePublic) GutendexApiService.PUBLIC_URL
        else GutendexApiService.BASE_URL

        val newUrl = original.url.newBuilder()
            .scheme("https")
            .host(baseUrl.removePrefix("https://").trimEnd('/'))
            .build()
        return chain.proceed(original.newBuilder().url(newUrl).build())
    }
}