package com.univ.doraboda.Service

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.univ.doraboda.R
import com.univ.doraboda.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SoundService: MediaSessionService() {
    var isReleased = false
    var mediaSession: MediaSession? = null
    var scope: Job? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate Service")
        val player: Player = ExoPlayer.Builder(this).build()
            .apply {
                repeatMode = REPEAT_MODE_ONE
            }

        val mediaItem1 =
            MediaItem.Builder()
                .setMediaId("media-1")
                .setUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sleepy_rain))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtist("Doraboda")
                        .setTitle("슬픈 선율")
                        .setArtworkUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.rain))
                        .build()
                )
                .build()

        val mediaItem2 =
            MediaItem.Builder()
                .setMediaId("media-2")
                .setUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.birds))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtist("Doraboda")
                        .setTitle("부드러운 선율")
                        .setArtworkUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.sunflower))
                        .build()
                )
                .build()

        player.setMediaItems(listOf(mediaItem1, mediaItem2))
        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        scope?.cancel()
                    }
                    else {
                        scope = CoroutineScope(Dispatchers.Default).launch {
                            delay(1000*5)
                            Timber.d("timer end")
                            withContext(Dispatchers.Main){
                                releaseAll()
                                stopSelf()
                            }
                        }
                    }
                }
            }
        )

        val pendingIntent = PendingIntent.getActivity(applicationContext, 1,
            Intent(applicationContext, MainActivity::class.java),
            FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(CustomCallback())
            .setSessionActivity(pendingIntent)
            .build()

        mediaSession!!.player.prepare()
        mediaSession!!.player.play()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession


    @UnstableApi
    private inner class CustomCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .build()
            val playerCommands =
                MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                    .remove(COMMAND_SEEK_TO_PREVIOUS)
                    .remove(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    .remove(COMMAND_SEEK_TO_NEXT)
                    .remove(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .build()
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .setAvailablePlayerCommands(playerCommands)
                .build()
        }
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle,
        ): ListenableFuture<SessionResult> {
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        releaseAll()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Timber.d("서비스 작동중지")
        if(!isReleased) releaseAll()
        super.onDestroy()
    }

    fun releaseAll(){
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        isReleased = true
    }

}