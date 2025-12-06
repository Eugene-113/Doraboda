package com.univ.doraboda.intent

sealed class SoundIntent {
    data class SeekAndPlayMusic(val position: Int): SoundIntent()
    data object Connect: SoundIntent()
    data object ReleaseIfConnected: SoundIntent()
}