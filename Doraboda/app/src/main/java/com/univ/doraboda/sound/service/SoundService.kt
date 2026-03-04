package com.univ.doraboda.sound.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.univ.doraboda.sound.data.musics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SoundService: MediaSessionService() {
    var isReleased = false
    @Inject
    lateinit var mediaSession: MediaSession
    val rJob = Job()
    var cancellableJob: Job? = null
    val scope = CoroutineScope(Dispatchers.Default + rJob)

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate Service")

        mediaSession!!.player.setMediaItems(musics)
        mediaSession!!.player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        cancellableJob?.cancel()
                    }
                    else {
                        cancellableJob = scope.launch {
                            delay(1000 * 8)
                            Timber.d("timer end")
                            withContext(Dispatchers.Main) {
                                releaseAll()
                                stopSelf()
                            }
                        }
                    }
                }
            }
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        releaseAll()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Timber.d("서비스 작동중지")
        if(!isReleased) releaseAll()
        scope.cancel()
        super.onDestroy()
    }

    fun releaseAll(){
        mediaSession?.run {
            player.release()
            release()
        }
        isReleased = true
    }

}