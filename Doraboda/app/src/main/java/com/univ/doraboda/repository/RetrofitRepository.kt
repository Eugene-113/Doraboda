package com.univ.doraboda.repository

import com.github.mikephil.charting.BuildConfig
import com.univ.doraboda.model.QuoteData
import com.univ.doraboda.network.RetrofitApi
import com.univ.doraboda.network.RetrofitFactory

class RetrofitRepository {
    val retrofitApi = RetrofitFactory.get().create(RetrofitApi::class.java)
    fun getQuote() = retrofitApi.getQuote(BuildConfig.apiKey, "happiness")
}