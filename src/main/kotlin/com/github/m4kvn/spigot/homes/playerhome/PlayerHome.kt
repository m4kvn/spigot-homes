package com.github.m4kvn.spigot.homes.playerhome

sealed class PlayerHome {
    abstract val owner: PlayerHomeOwner
    abstract val location: PlayerHomeLocation
    abstract val name: String?
    abstract val isPrivate: Boolean

    data class Default(
        override val owner: PlayerHomeOwner,
        override val location: PlayerHomeLocation,
        override val name: String? = null,
        override val isPrivate: Boolean = false,
    ) : PlayerHome()

    data class Named(
        override val owner: PlayerHomeOwner,
        override val location: PlayerHomeLocation,
        override val name: String,
        override val isPrivate: Boolean = false,
    ) : PlayerHome()

    data class Temporary(
        override val owner: PlayerHomeOwner,
        override val location: PlayerHomeLocation,
        override val name: String? = null,
        override val isPrivate: Boolean = true,
    ) : PlayerHome()
}

