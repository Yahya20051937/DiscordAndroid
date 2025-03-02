package com.client.discord.ui.component

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import androidx.wear.compose.material.Text

import com.client.discord.R
import com.client.discord.bl.model.Room
import com.client.discord.bl.viewModel.MainViewModel
import com.client.discord.bl.viewModel.server.VocalRoomViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VoiceChatComponent(room: Room, mainViewModel: MainViewModel, activity: Activity){
    val vocalRoomViewModel : VocalRoomViewModel = koinViewModel()
    val connectedMembers by room.connectedMembersState.collectAsState()
    Column (modifier = Modifier
        .fillMaxSize()
        .background(Color(34, 46, 50)))
        {


        Row {
            Image(
                painter = painterResource(id = R.drawable.baseline_keyboard_backspace_24),
                contentDescription = "back",
                modifier = Modifier.size(25.dp)
                    .offset(x = 0.dp)
                    .clickable {
                        mainViewModel.selectRoom(null)
                        // still connected.
                    }
            )

            Text(text = room.name, fontSize = 20.sp, modifier = Modifier.offset(x = 55.dp))
        }


        LazyColumn(modifier = Modifier.fillMaxWidth().height(640.dp).padding(top=20.dp)){
            items(room.getConnectedMembersPairs(connectedMembers.map { it })){
                Row (
                    modifier = Modifier.height(250.dp)
                ){
                    val offset = if (it.second == null) 100.dp else 0.dp
                    MemberVoiceChatComponent(
                        it.first,
                        modifier = Modifier.fillMaxHeight().fillParentMaxWidth(0.5f).offset(x=offset)
                            .border(width = 1.dp, color = Color.Black)
                    )
                    if (it.second != null)
                        MemberVoiceChatComponent(
                            it.second!!,
                            modifier = Modifier.fillMaxHeight().fillParentMaxWidth(0.5f)
                                .border(width = 1.dp, color = Color.Black)
                        )

                }
            }
        }

        VoiceCallPanelComponent(activity=activity, vocalRoomViewModel = vocalRoomViewModel)


    }
}