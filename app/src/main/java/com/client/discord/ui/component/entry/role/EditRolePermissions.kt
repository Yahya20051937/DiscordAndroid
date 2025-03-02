package com.client.discord.ui.component.entry.role

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.client.discord.R
import com.client.discord.bl.model.Permission
import com.client.discord.bl.model.Role
import com.client.discord.bl.request.RequestBodyD
import com.client.discord.bl.service.RoleService
import com.client.discord.bl.viewModel.server.PermissionsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun EditRolePermissions(role: Role, permissionsViewModel: PermissionsViewModel){
    val permissions = remember { mutableStateListOf<Permission>() }
    val roleService : RoleService = koinInject()
    val permissionsManagement by permissionsViewModel.rolesViewModel.clientPermissionsManagement.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            permissions.clear()
            permissions.addAll(roleService.getRolePermissions(role.id))
        }
    }

    Column {
        LazyColumn (Modifier.fillMaxHeight(0.8F))  {
            items(Permission.entries.toTypedArray()){
                val canClientEditPermission = permissionsManagement.hasPermission(it)   // the two other conditions will be checked before accessing this composable.
                Row (
                    Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = it.name,
                        modifier = Modifier.offset(x = 0.dp)
                    )

                    Image(
                        painter = painterResource(if (permissions.contains(it)) if (canClientEditPermission) R.drawable.baseline_check_box_24 else R.drawable.baseline_check_box_24_red else if (canClientEditPermission) R.drawable.baseline_check_box_outline_blank_24 else R.drawable.baseline_check_box_outline_blank_24_red),
                        contentDescription = "check",
                        modifier  = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                if (canClientEditPermission) {
                                    val request = RequestBodyD.AddRemovePermissionToRoleRequest(
                                        roleId = role.id,
                                        permission = it
                                    )
                                    if (!permissions.contains(it))
                                        permissionsViewModel.addPermission(request, permissions)
                                    else
                                        permissionsViewModel.removePermission(request, permissions)

                                }

                            }
                    )

                }
            }
        }

        Text(
            text = "Done",
            modifier = Modifier
                .padding(top = 5.dp, start = 105.dp)
                .height(20.dp)
                .width(50.dp)
                .background(Color(34, 46, 50))
                .clickable { permissionsViewModel.mainViewModel.updateEditingSettingsInRole(null) },
            color = Color.White,
            textAlign = TextAlign.Center
        )

    }


}