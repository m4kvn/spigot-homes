package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.color
import com.github.m4kvn.spigot.homes.usecase.GetTemporaryPlayerHomeUseCase
import com.github.m4kvn.spigot.homes.usecase.TeleportPlayerHomeUseCase
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomePreviousCommand : SubCommand {
    private val getTemporaryPlayerHomeUseCase by inject<GetTemporaryPlayerHomeUseCase>()
    private val teleportPlayerHomeUseCase by inject<TeleportPlayerHomeUseCase>()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        val playerHome = getTemporaryPlayerHomeUseCase(player) ?: return SubCommand.Response.Failed(MESSAGE_FAILED)
        teleportPlayerHomeUseCase(player, playerHome, savePrevious = false)
        return SubCommand.Response.Success
    }

    companion object {
        private val MESSAGE_FAILED = color(ChatColor.RED) { "No data of using the home command." }
    }
}