package com.client.discord.bl.model

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.client.discord.ui.BitService
import com.client.discord.ui.VoiceCallPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Transient
import java.util.LinkedList

class VoiceCallMember (
    name : String,
    mainRoleRanking: Int = Int.MAX_VALUE
) : Member(name, mainRoleRanking){
    val voiceChunks = LinkedList<VocalMessage>()
    var isConnected = true
    var play = false
    var isMuted = false
    var isSoundOff = false

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun playVocal(){
        val player = VoiceCallPlayer()
        this.play = true
        withContext(Dispatchers.IO) {
            player.start()     // when we change the room, connectedToRoom is null then changed. (check disconnect).
            while (isConnected && play) {
                val vocalMessage = voiceChunks.poll()
                if (vocalMessage != null)
                    player.play(vocalMessage.audioBytes64)
            }
            player.stop()
        }


    }

    fun handleClientDisconnection(){
        this.play = false
        this.voiceChunks.clear()
    }


}