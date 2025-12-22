package com.univ.doraboda.repository

import com.univ.doraboda.BuildConfig
import com.univ.doraboda.network.RetrofitApi
import com.univ.doraboda.network.RetrofitFactory
import javax.inject.Inject

class RetrofitRepository @Inject constructor(){
    val retrofitApi = RetrofitFactory.get().create(RetrofitApi::class.java)
    fun getQuote() = retrofitApi.getQuote(BuildConfig.apiKey, "happiness")
}