package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.univ.doraboda.model.QuoteData
import com.univ.doraboda.repository.DataStoreRepository
import com.univ.doraboda.repository.RetrofitRepository
import com.univ.doraboda.util.SoundController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(private val controller: SoundController, private val networkRepository: RetrofitRepository, private val dataStoreRepository: DataStoreRepository): ViewModel() {
    data class SoundState(val playState: Int, val isLoading: Boolean = true, val quote: String = "", val author: String = "", val isNetworkError: Boolean = false)
    sealed class SoundIntent {
        data class SeekAndPlayMusic(val position: Int): SoundIntent()
        data object ReleaseIfConnected: SoundIntent()
        object GetQuote: SoundIntent()
    }
    sealed class SoundResult{
        object Loading: SoundResult()
        data class PlayStateChanged(val playState: Int): SoundResult()
        data class QuoteLoaded(val quote: String, val author: String): SoundResult()
        object NetworkError: SoundResult()
    }
    private val _state = MutableStateFlow(SoundState(Player.STATE_IDLE))
    val state: StateFlow<SoundState> = _state.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>() //명언모드 에러 캐치
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            controller.state.collect {
                reduce(SoundResult.PlayStateChanged(it))
            }
        }
    }

    private fun reduce(result: SoundResult): SoundState{
        return when(result){
            is SoundResult.PlayStateChanged -> _state.value.copy(playState = result.playState)
            is SoundResult.NetworkError -> _state.value.copy(isNetworkError = true, isLoading = false)
            is SoundResult.Loading -> _state.value.copy(isLoading = true, isNetworkError = false)
            is SoundResult.QuoteLoaded -> _state.value.copy(quote = result.quote, author = result.author, isLoading = false, isNetworkError = false)
        }
    }

    fun handleIntent(intent: SoundIntent){ //각종 비동기처리
        when(intent){
            is SoundIntent.SeekAndPlayMusic -> seekAndPlay(intent.position)
            is SoundIntent.ReleaseIfConnected -> releaseIfConnected()
            is SoundIntent.GetQuote -> getQuote()
        }
    }

    private fun seekAndPlay(position: Int){
        controller.seekAndPlay(position)
    }

    private fun releaseIfConnected(){
        controller.releaseIfConnected()
    }

    private fun getQuote(){
        viewModelScope.launch {
            if(getQuoteSetting()){
                _state.value = reduce(SoundResult.Loading)
                val response = networkRepository.getQuote()
                response.enqueue(object : Callback<List<QuoteData>>{
                    override fun onResponse(
                        call: Call<List<QuoteData>?>,
                        response: Response<List<QuoteData>?>
                    ) {
                        if(response.isSuccessful() && response.body() != null){
                            val quoteData = response.body()!!.get(0)
                            _state.value = reduce(SoundResult.QuoteLoaded(quoteData.quote, quoteData.author))
                        }
                        else _state.value = reduce(SoundResult.NetworkError)
                    }
                    override fun onFailure(call: Call<List<QuoteData>?>, t: Throwable) {
                        _state.value = reduce(SoundResult.NetworkError)
                    }
                })
            }
        }
    }

    private suspend fun getQuoteSetting(): Boolean{
        try {
            val quoteMode = dataStoreRepository.getQuoteSetting().first()
            return quoteMode
        }catch (e: Exception){
            _errorEvents.emit(e.message.toString())
            return false
        }
    }
}