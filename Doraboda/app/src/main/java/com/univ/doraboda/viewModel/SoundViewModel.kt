package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.intent.SoundIntent
import com.univ.doraboda.state.SoundState
import com.univ.doraboda.util.SoundController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(val controller: SoundController): ViewModel() {
    private val eventChannel = Channel<SoundIntent>()
    val state = eventChannel.receiveAsFlow().runningFold(SoundState.Loading, ::reduce)
        .stateIn(viewModelScope, SharingStarted.Eagerly, SoundState.Loading)

    private fun reduce(cur: SoundState, intent: SoundIntent): SoundState{ //상태 변화
        return when(intent){
            is SoundIntent.SeekAndPlayMusic -> SoundState.SuccessToSeekAndPlay
            is SoundIntent.Connect -> SoundState.SuccessToConnect
            is SoundIntent.ReleaseIfConnected -> SoundState.SuccessToRelease
        }
    }

    fun handleIntent(intent: SoundIntent){ //각종 비동기처리
        viewModelScope.launch {
            when(intent){
                is SoundIntent.SeekAndPlayMusic -> seekAndPlay(intent.position)
                is SoundIntent.Connect -> connect()
                is SoundIntent.ReleaseIfConnected -> releaseIfConnected()
            }
            eventChannel.send(intent)
        }
    }

    fun seekAndPlay(position: Int){
        controller.seekAndPlay(position)
    }

    fun connect(){
        //controller.connect()
    }

    fun releaseIfConnected(){
        controller.releaseIfConnected()
    }
}