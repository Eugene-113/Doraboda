package com.univ.doraboda.sound.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

private val BASE_URL = "https://api.api-ninjas.com/v2/"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Named("QuoteRetrofit")
    @Provides
    @Singleton
    fun provideQuoteRetrofit(): Retrofit
    = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) //set converter
        .build()
}