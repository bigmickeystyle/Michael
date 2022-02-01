package com.mikesmith.michael.network

import retrofit2.Response
import retrofit2.http.GET

interface MichaelService {
    @GET("word")
    suspend fun getWordForToday(): Response<WordForTheDay>
}

data class WordForTheDay(val wordForToday: String)