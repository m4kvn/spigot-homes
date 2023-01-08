package com.github.m4kvn.spigot.homes.command.homes

import com.github.m4kvn.spigot.homes.command.core.CommandResponse
import com.github.m4kvn.spigot.homes.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.homes.usecase.GetTemporaryPlayerHomeUseCase
import com.github.m4kvn.spigot.homes.usecase.TeleportPlayerHomeUseCase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomesPreviousCommandExecutor : SubCommandExecutor() {
    private val getTemporaryPlayerHomeUseCase: GetTemporaryPlayerHomeUseCase by inject()
    private val teleportPlayerHomeUseCase: TeleportPlayerHomeUseCase by inject()

    override val flags: List<String> = listOf("-p", "--previous")
    override val usage: String = ""

    override fun onCommand(sender: CommandSender, args: List<String>): CommandResponse {
        val player = sender as? Player
            ?: return CommandResponse.Failed { INVALID_COMMAND_SENDER }
        if (args.isNotEmpty()) {
            return CommandResponse.Failed { INVALID_COMMAND_ARGUMENT_SIZE }
        }
        val playerHome = getTemporaryPlayerHomeUseCase(player)
            ?: return CommandResponse.Failed { NOT_FOUND_TEMPORARY_HOME }
        teleportPlayerHomeUseCase(player, playerHome, savePrevious = false)
        return CommandResponse.Success
    }

    override fun onTabComplete(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }
}