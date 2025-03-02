package com.client.discord.ui

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource

class AudioPlayer(context: Context) {
    private val exoPlayer = ExoPlayer.Builder(context).build()

    init {

        exoPlayer.prepare()
    }

    fun play(uri: Uri) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.play()
    }


    fun release() {
        exoPlayer.release()
    }
}