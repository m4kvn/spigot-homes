package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.command.core.BaseCommandExecutor
import com.github.m4kvn.spigot.homes.command.core.CommandResponse
import com.github.m4kvn.spigot.homes.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.homes.command.homes.HomesListCommandExecutor
import com.github.m4kvn.spigot.homes.command.homes.HomesPreviousCommandExecutor
import com.github.m4kvn.spigot.homes.command.homes.HomesRemoveCommandExecutor
import com.github.m4kvn.spigot.homes.command.homes.HomesSetCommandExecutor
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.usecase.TeleportPlayerHomeUseCase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomesCommandExecutor : BaseCommandExecutor() {
    private val teleportPlayerHomeUseCase: TeleportPlayerHomeUseCase by inject()
    private val playerHomeManager: PlayerHomeManager by inject()

    override val commandName: String = "homes"
    override val subCommands: List<SubCommandExecutor> = listOf(
        HomesSetCommandExecutor(),
        HomesRemoveCommandExecutor(),
        HomesListCommandExecutor(),
        HomesPreviousCommandExecutor(),
    )

    override fun onCommand(sender: CommandSender, args: Array<out String>): CommandResponse {
        val player = sender as? Player
            ?: return CommandResponse.Failed { INVALID_COMMAND_SENDER }
        if (args.size > 1) {
            return CommandResponse.Failed { INVALID_COMMAND_ARGUMENT_SIZE }
        }
        return if (args.isNotEmpty())
            teleportNamed(player, args.first()) else
            teleportDefault(player)
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>, index: Int?): List<String> {
        return emptyList()
    }

    private fun teleportDefault(player: Player): CommandResponse {
        val playerHome = playerHomeManager.getDefaultHome(player.uniqueId)
            ?: return CommandResponse.Failed { NOT_FOUND_DEFAULT_HOME }
        teleportPlayerHomeUseCase(player, playerHome)
        return CommandResponse.Success
    }

    private fun teleportNamed(player: Player, homeName: String): CommandResponse {
        val playerHome = playerHomeManager.getNamedHome(player.uniqueId, homeName)
            ?: return CommandResponse.Failed { "$NOT_FOUND_NAMED_HOME <$homeName>" }
        teleportPlayerHomeUseCase(player, playerHome)
        return CommandResponse.Success
    }
}