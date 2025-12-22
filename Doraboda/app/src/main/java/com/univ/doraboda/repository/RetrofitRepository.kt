package com.univ.doraboda.repository

import com.univ.doraboda.BuildConfig
import com.univ.doraboda.network.RetrofitApi
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class RetrofitRepository @Inject constructor(@Named("QuoteRetrofit") private val quoteRetrofit: Retrofit){
    val retrofitApi = quoteRetrofit.create(RetrofitApi::class.java)
    fun getQuote() = retrofitApi.getQuote(BuildConfig.apiKey, "happiness")
}