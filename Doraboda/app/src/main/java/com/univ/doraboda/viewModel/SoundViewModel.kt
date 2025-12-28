package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.univ.doraboda.util.SoundController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(val controller: SoundController): ViewModel() {
    data class SoundState(val playState: Int)
    sealed class SoundIntent {
        data class SeekAndPlayMusic(val position: Int): SoundIntent()
        data object ReleaseIfConnected: SoundIntent()
    }
    sealed class SoundResult{
        data class PlayStateChanged(val playState: Int): SoundResult()
    }
    private val _state = MutableStateFlow(SoundState(Player.STATE_IDLE))
    val state: StateFlow<SoundState> = _state

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
        }
    }

    fun handleIntent(intent: SoundIntent){ //각종 비동기처리
        when(intent){
            is SoundIntent.SeekAndPlayMusic -> seekAndPlay(intent.position)
            is SoundIntent.ReleaseIfConnected -> releaseIfConnected()
        }
    }

    fun seekAndPlay(position: Int){
        controller.seekAndPlay(position)
    }

    fun releaseIfConnected(){
        controller.releaseIfConnected()
    }
}