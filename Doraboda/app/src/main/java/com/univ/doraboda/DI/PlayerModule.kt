package com.univ.doraboda.DI

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.univ.doraboda.callback.CustomCallback
import com.univ.doraboda.view.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class PlayerModule {
    @Provides
    @ServiceScoped
    fun providePlayer(service: Service): ExoPlayer = ExoPlayer.Builder(service).build()
        .apply {
            repeatMode = REPEAT_MODE_ONE
        }

    @Provides
    @ServiceScoped
    @UnstableApi
    fun provideSession(service: Service, player: ExoPlayer, callback: CustomCallback, pendingIntent: PendingIntent): MediaSession
    = MediaSession.Builder(service, player)
        .setCallback(callback)
        .setSessionActivity(pendingIntent)
        .build()

    @Provides
    @ServiceScoped
    fun providePendingIntent(@ApplicationContext context: Context): PendingIntent =
        PendingIntent.getActivity(context, 1,
        Intent(context, MainActivity::class.java),
        FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}