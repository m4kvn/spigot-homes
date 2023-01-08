package com.github.m4kvn.spigot.homes.command.homes

import com.github.m4kvn.spigot.homes.command.core.CommandResponse
import com.github.m4kvn.spigot.homes.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.homes.messenger.appends
import com.github.m4kvn.spigot.homes.messenger.send
import com.github.m4kvn.spigot.homes.messenger.sendMessage
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeListData
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomesListCommandExecutor : SubCommandExecutor() {
    private val homeManager: PlayerHomeManager by inject()

    override val flags: List<String> = listOf("-l", "--list")
    override val usage: String = ""

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

    override fun onCommand(sender: CommandSender, args: List<String>): CommandResponse {
        val player = sender as? Player
            ?: return CommandResponse.Failed { INVALID_COMMAND_SENDER }
        if (args.isNotEmpty()) {
            return CommandResponse.Failed { INVALID_COMMAND_ARGUMENT_SIZE }
        }
        val data = homeManager.getPlayerHomeListData(player.uniqueId)
        if (data.isEmpty) {
            player.sendMessage { HOME_IS_EMPTY }
            return CommandResponse.Success
        }
        player.send {
            appendLine("${player.name}'s home list -----")
            data.default?.let {
                append("    ")
                appends(ChatColor.AQUA) { "default" }
                append(" ${it.position}\n")
            }
            data.namedList.forEach {
                append("    ")
                appends(ChatColor.GREEN) { it.name }
                append(" ${it.position}\n")
            }
        }
        return CommandResponse.Success
    }

    override fun onTabComplete(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }
}