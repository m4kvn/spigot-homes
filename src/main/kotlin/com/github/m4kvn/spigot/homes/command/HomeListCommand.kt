package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeListData
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomeListCommand : SubCommand {
    private val homeManager by inject<PlayerHomeManager>()

    private val PlayerHome.position: String
        get() = buildString {
            append("(")
            append("${ChatColor.GRAY}x=${ChatColor.RESET}")
            append(location.locationX.toInt())
            append("${ChatColor.GRAY}, y=${ChatColor.RESET}")
            append(location.locationY.toInt())
            append("${ChatColor.GRAY}, z=${ChatColor.RESET}")
            append(location.locationZ.toInt())
            append("${ChatColor.GRAY}, world=${ChatColor.RESET}")
            append(location.worldName)
            append(")")
        }

    private val PlayerHomeListData.isEmpty: Boolean
        get() = default == null && namedList.isEmpty()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        val data = homeManager.getPlayerHomeListData(player.uniqueId)
        if (data.isEmpty) {
            val message = "Your home is empty."
            return SubCommand.Response.Failed(message)
        }
        val message = buildString {
            appendLine("${player.name}'s home list -----")
            data.default?.let {
                append("    ")
                append("${ChatColor.AQUA}default${ChatColor.RESET}")
                append(" ${it.position}\n")
            }
            data.namedList.forEach {
                append("    ")
                append("${ChatColor.GREEN}${it.name}${ChatColor.RESET}")
                append(" ${it.position}\n")
            }
        }
        player.sendMessage(message)
        return SubCommand.Response.Success
    }
}