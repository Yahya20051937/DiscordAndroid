package com.client.discord.bl.model

import androidx.core.os.persistableBundleOf

class PermissionsManagement (
    private var clientPermissions : MutableList<Permission>,
    private var mainRoleRanking : Int
) {
    fun canDeleteRoom(room: Room) : Boolean{
        return clientPermissions.contains(Permission.DELETE_ROOM) && mainRoleRanking <= room.getMainRoleRanking()
    }

    fun canDeleteRoomPackage(roomPackage: RoomPackage) : Boolean{
        if (clientPermissions.contains(Permission.DELETE_ROOM)) {
            for (room in roomPackage.roomsTree.value.getAll())
                if (mainRoleRanking > room.getMainRoleRanking())
                    return false
            return true
        }
        return false
    }

    fun canDeleteRole(role: Role) : Boolean{
        return clientPermissions.contains(Permission.DELETE_ROLE) && mainRoleRanking < role.ranking
    }

    fun canAssignRole(role: Role) : Boolean{
        return clientPermissions.contains(Permission.ASSIGN_ROLE) && mainRoleRanking < role.ranking;
    }

    fun canEditRoomSettings(room: Room) : Boolean{
        return mainRoleRanking <= room.getMainRoleRanking()
    }

    fun canEditRoomScope(roleRanking:Int, scopeType: ScopeType, room: Room):Boolean{
        return this.mainRoleRanking < roleRanking && clientPermissions.contains(Permission.ADD_ROOM_SCOPE) && room.clientScopes.contains(scopeType)
    }

    fun getClientMainRoleRanking():Int{
        return mainRoleRanking
    }


    fun hasPermission(permission: Permission):Boolean{
        return clientPermissions.contains(permission)
    }

    fun addPermission(p : Permission){
        if (!this.hasPermission(p))
            clientPermissions.add(p)
    }

    fun removePermission(p : Permission){
        clientPermissions.remove(p)
    }

    fun updatePermissions(to:MutableList<Permission>){
        clientPermissions = to
    }

    fun updateMainRoleRanking(to:Int){
        mainRoleRanking = to
    }

    fun copy() : PermissionsManagement{
        return PermissionsManagement(clientPermissions, mainRoleRanking)
    }

    fun getPermissions(): List<Permission> {
        return clientPermissions
    }


}