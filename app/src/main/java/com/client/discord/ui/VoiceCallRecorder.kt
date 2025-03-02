package com.client.discord.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDescriptor
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileDescriptor
import java.io.OutputStream

object VoiceCallRecorder {
    private const val sampleRate = 44100
    private const val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private const val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private var isRecording = true
    var isMuted = false
    private var audioRecord:AudioRecord? = null

    suspend fun record(activity: Activity, file: File){
        withContext(Dispatchers.IO) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isRecording = true
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )

                val byteArray = ByteArray(bufferSize)

                audioRecord?.startRecording()


                while (isRecording) {
                    if (!isMuted) {
                        val read = audioRecord?.read(byteArray, 0, bufferSize)
                        if (read != null && read > 0)
                            file.appendBytes(byteArray)
                    }


                }
            }
        }
    }

    fun stop(){
        isRecording = false
        audioRecord?.stop()
        audioRecord = null


    }



}