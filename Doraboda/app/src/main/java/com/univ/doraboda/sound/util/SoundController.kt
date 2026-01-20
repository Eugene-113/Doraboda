package com.univ.doraboda.sound.util

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.univ.doraboda.sound.service.SoundService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundController @Inject constructor(@ApplicationContext private val context: Context) {
    private val _state = MutableStateFlow(Player.STATE_IDLE)
    val state: StateFlow<Int> = _state.asStateFlow()
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    suspend fun connect(){ //서비스와 컨트롤러 연결
        val sessionToken = SessionToken(
            context,
            ComponentName(context, SoundService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).setListener(
            object : MediaController.Listener {
                override fun onDisconnected(controller: MediaController) {
                    releaseIfConnected() //서비스 자동 종료되면 컨트롤러 release
                }
            }
        ).buildAsync()
        controller = controllerFuture!!.await()
        controller!!.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _state.value = playbackState
                }

                override fun onPlayerError(error: PlaybackException) {
                    _state.value = error.errorCode
                }
            })
        prepare()
    }

    fun prepare(){
        controller!!.prepare()
    }

    fun seekAndPlay(position: Int){
        scope.launch {
            if(controller == null) connect() //컨트롤러 연결이 끊어졌는데 컨트롤러는 있는 경우 / 연결도 없고 컨트롤러도 없는 경우
            controller!!.seekTo(position, 0)
            if(!controller!!.isPlaying) controller!!.play()
        }
    }

    fun releaseIfConnected(){
        if (controller != null){
            controller = null
            MediaController.releaseFuture(controllerFuture!!)
            controllerFuture = null
            scope.cancel()
        }
    }
}