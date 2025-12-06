package com.univ.doraboda.state

sealed class SoundState {
    data object Loading : SoundState()
    data object SuccessToSeekAndPlay : SoundState()
    data object SuccessToConnect : SoundState()
    data object SuccessToRelease : SoundState()
}