package com.client.discord.ui.component.entry.scope

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.client.discord.R
import com.client.discord.bl.model.Room
import com.client.discord.bl.model.RoomScope
import com.client.discord.bl.service.RoleService
import com.client.discord.bl.viewModel.server.RolesViewModel
import com.client.discord.bl.viewModel.server.RoomScopesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun RemoveScopeComponent(room: Room, rolesViewModel: RolesViewModel, roomScopesViewModel: RoomScopesViewModel){
    val roleService : RoleService = koinInject()
    val scopes = remember { mutableStateListOf<RoomScope>() }
    val permissionsManagement by rolesViewModel.clientPermissionsManagement.collectAsState()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){
            scopes.clear()
            scopes.addAll(roleService.requestRoomScopes(room.id))
        }
    }


    Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp, top=10.dp)) {
        Row (
            Modifier.fillMaxWidth()
            .height(60.dp)
            .border(width = 1.dp, color = Color.Black))
        {
            Text(
                "role",
                modifier = Modifier.offset(x = 15.dp).padding(top = 20.dp).width(60.dp),
                fontSize = 17.sp
            )

            Text(
                "scope",
                modifier = Modifier.width(50.dp).offset(x = 50.dp).padding(top = 20.dp),
                fontSize = 17.sp
            )
        }

        LazyColumn  {
            items(scopes){
                val role = rolesViewModel.findRoleByRanking(it.roleRanking)
                if (role != null)
                    Row(
                        Modifier.fillMaxWidth()
                            .height(70.dp)
                            .border(width = 1.dp, color = Color.Black)
                    ) {
                        Text(
                            text = role.name,
                            modifier = Modifier.offset(x = 15.dp).padding(top = 20.dp).width(70.dp)
                        )

                        Text(
                            text = it.scopeType.name,
                            modifier = Modifier.offset(x = 50.dp).padding(top = 20.dp).width(60.dp)
                        )
                        val canDeleteRoomScope = permissionsManagement.canEditRoomScope(it.roleRanking, it.scopeType, room)
                        Image(
                            painter = painterResource(if (canDeleteRoomScope) R.drawable.baseline_delete_24 else R.drawable.baseline_delete_24_red),
                            contentDescription = "delete",
                            modifier = Modifier.offset(x = 75.dp).padding(top = 20.dp)
                                .clickable {
                                    if (canDeleteRoomScope)
                                        roomScopesViewModel.deleteRoomScope(it, scopes)
                                }
                        )

                    }
            }
        }
    }


}


