package com.univ.doraboda.state

sealed class NetworkState {
    data object Loading : ReadModeState()
    data class SuccessToGetQuote(val quote: String, val author: String) : NetworkState()
}