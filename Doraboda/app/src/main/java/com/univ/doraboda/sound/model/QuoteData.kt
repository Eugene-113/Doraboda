package com.univ.doraboda.sound.model

data class QuoteData(
    val quote: String,
    val author: String,
    val work: String,
    val categories: List<String>
    )