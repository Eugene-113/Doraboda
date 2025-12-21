package com.univ.doraboda.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private val BASE_URL = "https://api.api-ninjas.com/v2/"
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) //set converter
        .build()
    fun get(): Retrofit{
        return retrofit
    }
}