package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.model.QuoteData
import com.univ.doraboda.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(private val networkRepository: RetrofitRepository): ViewModel() {
    data class NetworkState(val isLoading: Boolean = true, val quote: String = "", val author: String = "", val isError: Boolean = false)
    sealed class NetworkIntent {
        object GetQuote: NetworkIntent()
    }
    sealed class NetworkResult{
        object Loading: NetworkResult()
        data class Success(val quote: String, val author: String): NetworkResult()
        object Error: NetworkResult()
    }
    private val _state = MutableStateFlow(NetworkState())
    val state: StateFlow<NetworkState> = _state

    private fun reduce(result: NetworkResult): NetworkState{
        return when(result){
            is NetworkResult.Loading -> _state.value.copy(isLoading = true)
            is NetworkResult.Success -> _state.value.copy(isLoading = false, quote = result.quote, author = result.author)
            is NetworkResult.Error -> _state.value.copy(isLoading = false)
        }
    }

    fun handleIntent(intent: NetworkIntent){ //각종 비동기처리
        viewModelScope.launch {
            when(intent){
                is NetworkIntent.GetQuote -> getQuote()
            }
        }
    }

    private fun getQuote(){
        _state.value = reduce(NetworkResult.Loading)
        val response = networkRepository.getQuote()
        response.enqueue(object : Callback<List<QuoteData>>{
            override fun onResponse(
                call: Call<List<QuoteData>?>,
                response: Response<List<QuoteData>?>
            ) {
                if(response.isSuccessful() && response.body() != null){
                    val quoteData = response.body()!!.get(0)
                    _state.value = reduce(NetworkResult.Success(quoteData.quote, quoteData.author))
                }
                else _state.value = reduce(NetworkResult.Error)
            }
            override fun onFailure(call: Call<List<QuoteData>?>, t: Throwable) {
                _state.value = reduce(NetworkResult.Error)
            }
        })
    }
}