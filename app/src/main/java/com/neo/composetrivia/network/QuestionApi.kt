package com.neo.composetrivia.network

import com.neo.composetrivia.model.Question
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface QuestionApi {

    @GET("world.json")
    suspend fun getAllQuestions(): Question
}