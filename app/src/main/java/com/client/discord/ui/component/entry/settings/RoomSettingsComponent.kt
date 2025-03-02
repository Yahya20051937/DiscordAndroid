package com.client.discord.ui.component.entry.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.client.discord.R
import com.client.discord.bl.model.Room
import com.client.discord.bl.model.RoomAccessType
import com.client.discord.bl.viewModel.server.RolesViewModel
import com.client.discord.bl.viewModel.server.RoomScopesViewModel
import com.client.discord.bl.viewModel.server.RoomsViewModel
import com.client.discord.ui.component.entry.scope.AddScopeComponent
import com.client.discord.ui.component.entry.scope.RemoveScopeComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun RoomSettingsComponent(room: Room, roomsViewModel: RoomsViewModel, modifier: Modifier, rolesViewModel: RolesViewModel){
    var accessType by remember { mutableStateOf(room.roomAccessType) }
    var task by remember { mutableIntStateOf(0) }
    val  roomScopeViewModel : RoomScopesViewModel = koinViewModel()
    Column (modifier) {
        Row {
            Row {
                androidx.compose.material3.Text(
                    text = "Room ${room.name} settings",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 50.dp, top = 5.dp)
                        .fillMaxWidth(0.8f),

                    )

                Image(
                    painter = painterResource(id = R.drawable.baseline_close_24),
                    contentDescription = "close",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            roomsViewModel.mainViewModel.updateEditingSettingsInRoom(null)
                        },
                    alignment = Alignment.CenterEnd

                )


            }
        }

        when (task){
            0 -> Column  (modifier=Modifier.padding(top = 20.dp, start = 20.dp).fillMaxSize()) {
                Text(
                    text = "Make it "  + if (accessType == RoomAccessType.PUBLIC) "private" else  "public" + " Not implemented",
                    fontSize = 17.sp,
                    modifier = Modifier.offset(x = 50.dp).width(120.dp)
                        .height(65.dp)
                        .background(Color(34, 46, 50))
                        .padding(top = 15.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center

                )
                if (accessType == RoomAccessType.PRIVATE){
                    Text(
                        text = "Add Scope",
                        fontSize = 17.sp,
                        modifier = Modifier.offset(x = 50.dp).padding(top = 20.dp).width(120.dp)
                            .height(65.dp)
                            .background(Color(34, 46, 50))
                            .padding(top = 15.dp)
                            .clickable { task =  1 },
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Delete Scope",
                        fontSize = 17.sp,
                        modifier = Modifier.offset(x = 50.dp).padding(top = 20.dp).width(120.dp)
                            .height(65.dp)
                            .background(Color(34, 46, 50))
                            .padding(top = 15.dp)
                            .clickable { task =  2 },
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = "Rename (Not implemented)",
                    fontSize = 17.sp,
                    modifier = Modifier.offset(x = 50.dp).padding(top = 20.dp).width(120.dp)
                        .height(65.dp)
                        .background(Color(34, 46, 50))
                        .padding(top = 15.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

            }

            1 -> {
                AddScopeComponent(room=room, roomScopesViewModel=roomScopeViewModel, rolesViewModel = rolesViewModel)
            }

            2 -> {
                RemoveScopeComponent(room=room, rolesViewModel=rolesViewModel, roomScopesViewModel=roomScopeViewModel)
            }
        }






    }


}