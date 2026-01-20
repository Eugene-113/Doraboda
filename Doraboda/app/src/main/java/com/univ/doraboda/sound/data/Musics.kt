package com.univ.doraboda.sound.data

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.univ.doraboda.R

val packageName = "com.univ.doraboda"
val musics = listOf(
    MediaItem.Builder()
        .setMediaId("media-1")
        .setUri(Uri.parse("android.resource://" + packageName + "/" + R.raw.sleepy_rain))
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setArtist("Doraboda")
                .setTitle("슬픈 선율")
                .setArtworkUri(Uri.parse("android.resource://" + packageName + "/" + R.drawable.rain))
                .build())
        .build(),
    MediaItem.Builder()
        .setMediaId("media-2")
        .setUri(Uri.parse("android.resource://" + packageName + "/" + R.raw.birds))
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setArtist("Doraboda")
                .setTitle("부드러운 선율")
                .setArtworkUri(Uri.parse("android.resource://" + packageName + "/" + R.drawable.sunflower))
                .build()
        )
        .build()
)