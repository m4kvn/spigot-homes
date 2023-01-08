package com.github.m4kvn.spigot.homes.command.homes

import com.github.m4kvn.spigot.homes.Constants
import com.github.m4kvn.spigot.homes.command.core.CommandResponse
import com.github.m4kvn.spigot.homes.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.usecase.RemoveDefaultHomeUseCase
import com.github.m4kvn.spigot.homes.usecase.RemoveNamedHomeUseCase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomesRemoveCommandExecutor : SubCommandExecutor() {
    private val playerHomeManager: PlayerHomeManager by inject()
    private val removeDefaultHomeUseCase: RemoveDefaultHomeUseCase by inject()
    private val removeNamedHomeUseCase: RemoveNamedHomeUseCase by inject()

    override val flags: List<String> = listOf("-r", "--remove")
    override val usage: String = "( ${Constants.DEFAULT_HOME_NAME} | <home_name> )"

    override fun onCommand(sender: CommandSender, args: List<String>): CommandResponse {
        val player = sender as? Player
            ?: return CommandResponse.Failed { INVALID_COMMAND_SENDER }
        if (args.size != 1) {
            return CommandResponse.Failed { INVALID_COMMAND_ARGUMENT_SIZE }
        }
        val homeName = args.first()
        return if (homeName == Constants.DEFAULT_HOME_NAME)
            removeDefaultHome(player) else
            removeNamedHome(player, homeName)
    }

    override fun onTabComplete(sender: CommandSender, args: List<String>): List<String> {
        val player = sender as? Player ?: return emptyList()
        if (args.lastIndex > 1) return emptyList()
        val homeListData = playerHomeManager.getPlayerHomeListData(player.uniqueId)
        return buildList {
            if (homeListData.default != null) {
                add(Constants.DEFAULT_HOME_NAME)
            }
            addAll(homeListData.namedList.map { it.name })
        }
    }

    private fun removeDefaultHome(player: Player): CommandResponse {
        removeDefaultHomeUseCase(player)
            ?: return CommandResponse.Failed { NOT_FOUND_DEFAULT_HOME }
        return CommandResponse.Success
    }

    private fun removeNamedHome(player: Player, homeName: String): CommandResponse {
        removeNamedHomeUseCase(player, homeName)
            ?: CommandResponse.Failed { "$NOT_FOUND_NAMED_HOME <$homeName>" }
        return CommandResponse.Success
    }
}