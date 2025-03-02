package com.client.discord.ui.component

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.client.discord.R
import com.client.discord.bl.viewModel.server.VocalRoomViewModel

@Composable
fun VoiceCallPanelComponent(vocalRoomViewModel: VocalRoomViewModel, activity: Activity) {
    val muted by vocalRoomViewModel.muted.collectAsState()
    val soundOff by vocalRoomViewModel.soundOff.collectAsState()
    Row (
        modifier = Modifier.fillMaxWidth()
            .fillMaxHeight()
            .background(Color.DarkGray)
    ) {
        Surface (
            modifier = Modifier.offset(x=50.dp).size(50.dp).clickable { vocalRoomViewModel.updateMuted() },
            shape = CircleShape,
            color = Color.White

        ) {
            Image(
                painter =  painterResource(if (muted) R.drawable.miciconmuted else R.drawable.baseline_mic_24_b),
                contentDescription = "",
                contentScale = ContentScale.Crop

            )
        }

        Surface (
            modifier = Modifier.offset(x=100.dp).size(50.dp).clickable { vocalRoomViewModel.updateSoundOff() },
            shape = CircleShape,
            color = Color.Black
        ) {
            Image(
                painter = painterResource(if (!soundOff) R.drawable.sound_on else R.drawable.sound_off),
                contentDescription = "",
                contentScale = ContentScale.Crop

            )
        }

        Surface (
            modifier = Modifier.offset(x=150.dp).size(50.dp).clickable { vocalRoomViewModel.disconnect() },
            shape = CircleShape,
            color = Color.Red
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_call_end_24),
                contentDescription = "",
                contentScale = ContentScale.Crop

            )
        }
    }




}