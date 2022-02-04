package com.mikesmith.michael.network

import retrofit2.Response
import retrofit2.http.*

interface MichaelService {
    @GET("/word")
    suspend fun getWordForToday(): Response<WordForTheDay>

    @Headers("Content-Type: application/json")
    @POST("/word")
    suspend fun setWordForToday(@Body body: Map<String, String>): Response<Unit>
}

data class WordForTheDay(val wordForToday: String)