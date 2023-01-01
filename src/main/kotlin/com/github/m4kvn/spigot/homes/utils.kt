package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeChunk
import org.bukkit.Chunk

inline val Chunk.asPlayerHomeChunk: PlayerHomeChunk
    get() = PlayerHomeChunk(x, z, world.uid)