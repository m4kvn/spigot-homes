package com.github.m4kvn.spigot.homes.playerhome

data class PlayerHomeListData(
    val default: PlayerHome.Default?,
    val namedList: List<PlayerHome.Named>,
)