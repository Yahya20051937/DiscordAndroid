package com.client.discord.bl.model

import kotlinx.serialization.Serializable

@Serializable
class VocalMessage  (
    val roomId:String,
    val audioBytes64 : String,
    val speaker  :String
) {
    constructor(m : Map<String, String>) : this(m["roomId"]!!, m["audioBytes64"]!!, m["speaker"]!!)

}