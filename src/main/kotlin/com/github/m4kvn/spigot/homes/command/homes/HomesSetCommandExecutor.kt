package com.github.m4kvn.spigot.homes.command.homes

import com.github.m4kvn.spigot.homes.Constants
import com.github.m4kvn.spigot.homes.command.core.CommandResponse
import com.github.m4kvn.spigot.homes.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.homes.usecase.SetDefaultPlayerHomeUseCase
import com.github.m4kvn.spigot.homes.usecase.SetNamedPlayerHomeUseCase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomesSetCommandExecutor : SubCommandExecutor() {
    private val setDefaultPlayerHomeUseCase: SetDefaultPlayerHomeUseCase by inject()
    private val setNamedPlayerHomeUseCase: SetNamedPlayerHomeUseCase by inject()

    override val flags: List<String> = listOf("-s", "--set")
    override val usage: String = "( ${Constants.DEFAULT_HOME_NAME} | <home_name> )"

    override fun onCommand(sender: CommandSender, args: List<String>): CommandResponse {
        val player = sender as? Player
            ?: return CommandResponse.Failed { INVALID_COMMAND_SENDER }
        if (args.size != 1) {
            return CommandResponse.Failed { INVALID_COMMAND_ARGUMENT_SIZE }
        }
        val homeName = args.first()
        if (homeName == Constants.DEFAULT_HOME_NAME)
            setDefaultPlayerHomeUseCase(player) else
            setNamedPlayerHomeUseCase(player, homeName)
        return CommandResponse.Success
    }

    override fun onTabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (sender !is Player) return emptyList()
        if (args.lastIndex > 1) return emptyList()
        return listOf(Constants.DEFAULT_HOME_NAME)
    }
}