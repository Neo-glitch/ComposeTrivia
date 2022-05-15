package com.neo.composetrivia.di

import com.neo.composetrivia.network.QuestionApi
import com.neo.composetrivia.repository.QuestionRepository
import com.neo.composetrivia.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // provides the question api service for use case
    @Singleton
    @Provides
    fun provideQuestionApi(): QuestionApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuestionApi::class.java)


    // provides question repo for work
    // not needed since the class constructor is already injected
    @Singleton
    @Provides
    fun providesQuestionRepository(api: QuestionApi) = QuestionRepository(api)
}