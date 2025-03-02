package com.client.discord.bl.viewModel.server

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.AudioTrack
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.client.discord.R
import com.client.discord.bl.model.Room
import com.client.discord.bl.model.RoomPackage
import com.client.discord.bl.model.RoomType
import com.client.discord.bl.model.VocalMessage
import com.client.discord.bl.service.ApiService
import com.client.discord.bl.viewModel.MainViewModel
import com.client.discord.ui.BitService
import com.client.discord.ui.VoiceCallPlayer
import com.client.discord.ui.VoiceCallRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream
import java.util.Collections
import java.util.LinkedList
import java.util.Queue
import kotlin.math.min


class VocalRoomViewModel (
) : ViewModel(), KoinComponent {


    val apiService : ApiService by inject()
    val mainViewModel : MainViewModel by inject()
    val rolesViewModel : RolesViewModel by inject()

    private var connectedToRoom : Room? = null;

    private val _muted = MutableStateFlow(false)
    val muted : StateFlow<Boolean> =_muted

    private val _soundOff = MutableStateFlow(false)
    val soundOff : StateFlow<Boolean> = _soundOff

    fun updateMuted(){
        VoiceCallRecorder.isMuted = !_muted.value
        _muted.value = !_muted.value
        recordingFile?.writeBytes(ByteArray(0))
    }

    fun updateSoundOff(){
        _soundOff.value = !_soundOff.value
    }


    private var webSocket : WebSocket? = null


    private var recordingFile : File?  =  null

    private val recordingInterval = 50

    fun select(room: Room, activity: Activity){
        if (room != connectedToRoom) {
            if (room.roomType == RoomType.VOCAL && room.canClientJoin.value) {
                if (room.id != this.connectedToRoom?.id) {
                    this.disconnect()
                    this.establishConnection(activity, room)
                }

            }
        }
        else
            mainViewModel.selectRoom(room)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleConnection(room: Room, activity: Activity){
        VoiceCallRecorder.stop()
        this.connectedToRoom = room;  // connect and select the room.
        mainViewModel.selectRoom(room)
        this.recordingFile = File(activity.cacheDir, "vocal-room-connection.mp3")
        connectedToRoom?.connectMember(apiService.jwt.username)
        this.playJoinSound(activity.applicationContext)
        viewModelScope.launch {
            record(activity)
        }
        viewModelScope.launch {
            play()
        }
    }

    fun disconnect(){
        this.webSocket?.cancel()
    }

    fun handleDisconnection(activity: Activity, playSound : Boolean = true){
        this.webSocket?.cancel()
        if (playSound)
            this.playQuitSound(activity.applicationContext)
        recordingFile?.writeBytes(ByteArray(0))
        VoiceCallRecorder.stop()
        if (connectedToRoom != null) {
            connectedToRoom!!.disconnectMember(apiService.jwt.username)
            for (member in connectedToRoom!!.getMembers())
                member.handleClientDisconnection()
        }
        mainViewModel.selectRoom(null)
        if (connectedToRoom?.id == this.connectedToRoom?.id )// if this connectedToRoom is one the selected, we deselect it.
            this.connectedToRoom = null

    }

    private fun establishConnection(activity: Activity, room: Room){
        try {
            val authorization = apiService.jwt.getAuthorization()
            val request = Request.Builder()
                .url("ws://${apiService.host}:${apiService.socketPort}/room-connection/ws/${room.id}")
                .header("Authorization", authorization)
                .build();
            val listener = object : WebSocketListener() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val message : VocalMessage = Json.decodeFromString(text)
                        if (!_soundOff.value && message.roomId == connectedToRoom!!.id && message.speaker != apiService.jwt.username) // just for safety, both should be true
                            connectedToRoom!!.addChunkToMember(message.speaker, message)
                    } catch (e : Exception){
                        print(text)
                        e.printStackTrace()
                    }
                }


                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: okhttp3.Response?
                ) {
                    println("WebSocket connection failed")
                    this@VocalRoomViewModel.webSocket = null
                    handleDisconnection(activity)
                    t.printStackTrace()
                }

                @SuppressLint("NewApi")
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    handleConnection(room, activity)
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    println("Closing with code $code and reason $reason")
                    super.onClosing(webSocket, code, reason)
                    handleDisconnection(activity)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    println("WebSocket connection closed.")
                    super.onClosed(webSocket, code, reason)
                    handleDisconnection(activity)
                }


            }
            webSocket = apiService.client.newWebSocket(request, listener)


        } catch (e : Exception){e.printStackTrace()}

    }



    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun record(activity: Activity){
        viewModelScope.launch {
            VoiceCallRecorder.record(
                activity,
                recordingFile!!
            )}
        withContext(Dispatchers.IO) {
            var start = System.currentTimeMillis()
            while (connectedToRoom != null) {
                if (!_muted.value)
                    if (System.currentTimeMillis() - start > recordingInterval)
                        try {
                            val recordedBytes = recordingFile!!.inputStream().use { it.readBytes() }
                            val bytes = BitService.bytesToBase64(recordedBytes)
                            recordingFile!!.writeBytes(ByteArray(0))
                            val vocalMessage = VocalMessage(
                                roomId = connectedToRoom!!.id,
                                audioBytes64 = bytes,
                                speaker = apiService.jwt.username
                            )


                            webSocket?.send(Json.encodeToString(vocalMessage))
                            start = System.currentTimeMillis()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

            }

            VoiceCallRecorder.stop()
            webSocket?.cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun play(){
        val members = connectedToRoom!!.getMembers()
        for (member in members)
            viewModelScope.launch { // each member of hte room has its own thread.
                member.playVocal()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleMemberConnection(roomPackage: RoomPackage, roomId:String, username:String, context: Context){
        val room = roomPackage.findRoomById(roomId)
        room?.connectMember(username)
        if (room?.isMemberConnected(apiService.jwt.username) == true) {
            this.playJoinSound(context)
            viewModelScope.launch {
                val member = room.findMember(username)!!
                member.isConnected = true
                member.playVocal()
            }

        }
    }

    fun handleMemberDisconnection(roomPackage: RoomPackage, roomId: String, username: String, context: Context){
        val room = roomPackage.findRoomById(roomId)
        room?.disconnectMember(username)
        if (room?.isMemberConnected(apiService.jwt.username) == true) {
            this.playQuitSound(context)
            room.findMember(username)?.isConnected = false

        }
    }




    private fun playJoinSound(context: Context){
        val uri = Uri.parse("android.resource://${context.packageName}/raw/discord_join")
        mainViewModel.playAudio(uri, context)
    }

    private fun playQuitSound(context: Context){
        val uri = Uri.parse("android.resource://${context.packageName}/raw/discord_leave")
        mainViewModel.playAudio(uri, context)
    }








}