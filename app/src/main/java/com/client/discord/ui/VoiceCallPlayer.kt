package com.client.discord.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.io.encoding.Base64

class VoiceCallPlayer {
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = 1024
    private var audioTrack : AudioTrack? = null




    fun start(){
        audioTrack = AudioTrack(
            AudioManager.STREAM_VOICE_CALL,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize,
            AudioTrack.MODE_STREAM
        )
        audioTrack!!.play()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun play(bytes64:String){
        val bytes = java.util.Base64.getDecoder().decode(bytes64)
        audioTrack?.write(bytes, 0, bytes.size)

        }


    fun stop(){
        audioTrack?.stop()
        audioTrack?.release()
    }


}