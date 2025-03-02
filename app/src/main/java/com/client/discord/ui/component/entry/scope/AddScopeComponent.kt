package com.client.discord.ui.component.entry.scope

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.discord.R
import com.client.discord.bl.model.Role
import com.client.discord.bl.model.Room
import com.client.discord.bl.model.ScopeType
import com.client.discord.bl.request.RequestBodyD
import com.client.discord.bl.viewModel.server.RolesViewModel
import com.client.discord.bl.viewModel.server.RoomScopesViewModel

@Composable
fun AddScopeComponent(room:Room, roomScopesViewModel: RoomScopesViewModel, rolesViewModel: RolesViewModel) {
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var selectedScope by remember { mutableStateOf<ScopeType?>(null) }
    var rolesDrop by remember { mutableStateOf(false) }
    var scopesDrop by remember { mutableStateOf(false) }
    Row (
        modifier = Modifier.padding(top = 0.dp, start = 19.dp)
    ) {
        Column (
            modifier = Modifier.border(width =  0.5.dp, color = Color.Black)
                .width(120.dp)
        ) {
            Row (
                modifier = Modifier.height(30.dp)
            ) {

                androidx.compose.material3.Text(
                    text = if (selectedRole != null) selectedRole!!.name else "Select a role",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 5.dp, start = 3.dp)
                )

                Image(
                    painter = painterResource(id = if (rolesDrop) R.drawable.baseline_arrow_drop_down_24 else R.drawable.baseline_arrow_right_24),
                    contentDescription = "dropDownRoom",
                    modifier = Modifier.clickable {
                        rolesDrop = !rolesDrop
                    }
                        .padding(start = 23.dp, top = 5.dp)
                        .size(20.dp)
                )
            }

            if (rolesDrop)
                LazyColumn (
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                ) {
                    items(rolesViewModel.getRolesBelowClientRanking()) {
                        androidx.compose.material3.Text(
                            text = it.name,
                            modifier = Modifier.clickable {
                                selectedRole = it
                                selectedScope = null
                                rolesDrop = false
                                scopesDrop = false
                            }
                        )
                    }
                }
        }

        Column (
            modifier = Modifier.border(width =  0.5.dp, color = Color.Black)
                .width(120.dp)
        ) {
            Row (
                modifier = Modifier
                    .height(30.dp)
            ) {

                androidx.compose.material3.Text(
                    text = if (selectedScope != null) selectedScope!!.name else "Select a scope",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 5.dp, start = 3.dp)
                )

                Image(
                    painter = painterResource(id = if (scopesDrop) R.drawable.baseline_arrow_drop_down_24 else R.drawable.baseline_arrow_right_24),
                    contentDescription = "dropDownScopeType",
                    modifier = Modifier.clickable {
                        scopesDrop = !scopesDrop
                    }

                        .padding(start = 20.dp, top = 5.dp)
                        .size(20.dp)
                )
            }

            if (scopesDrop)
                LazyColumn {
                    items(
                        room.clientScopes
                    ) {
                        androidx.compose.material3.Text(
                            text = it.toString(),
                            modifier = Modifier.clickable {
                                selectedScope = it
                                scopesDrop = false
                            }
                        )
                    }
                }
        }

        Image(
            painter = painterResource(id = R.drawable.baseline_add_24),
            contentDescription = "add",
            modifier = Modifier
                .padding(top = 5.dp)
                .size(20.dp)
                .clickable {
                    if (selectedRole != null && selectedScope != null)
                        roomScopesViewModel.addRoomScope(
                            RequestBodyD.AddRoomScopeRequest(
                                roomId = room.id,
                                roleId = selectedRole!!.id,
                                scopeType = selectedScope!!
                            )
                        )
                }
        )
    }

}