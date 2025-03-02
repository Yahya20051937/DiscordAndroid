package com.client.discord.bl.model.dto

import com.client.discord.bl.model.Room
import com.client.discord.bl.model.ScopeType
import kotlinx.serialization.Serializable

@Serializable
class RoomClientScopesDto (
    val roomPackageId:String,
    val roomId:String,
    val clientScopes: List<ScopeType>
) {
    constructor(room: Room) : this(
        roomId = room.id,
        roomPackageId = room.roomPackageId,
        clientScopes = listOf()
    )

}