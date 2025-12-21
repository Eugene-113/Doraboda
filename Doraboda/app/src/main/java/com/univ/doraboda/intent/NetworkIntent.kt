package com.univ.doraboda.intent

sealed class NetworkIntent {
    data object GetQuote: NetworkIntent()
}