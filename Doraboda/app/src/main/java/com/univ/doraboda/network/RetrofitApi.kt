package com.univ.doraboda.network

import com.univ.doraboda.model.QuoteData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitApi {
    @GET("randomquotes") //엔드포인트
    fun getQuote(
        @Header("X-Api-Key")
        key: String,
        @Query("category")
        category: String
    ): Call<List<QuoteData>>
}