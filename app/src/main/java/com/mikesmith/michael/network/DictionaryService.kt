package com.mikesmith.michael.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryService {
    @GET("api/v2/entries/en/{word}")
    suspend fun checkValidity(@Path("word") word: String): Response<List<Definition>>
}