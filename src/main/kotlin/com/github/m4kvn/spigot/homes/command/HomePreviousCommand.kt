package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.messenger.appendMessage
import com.github.m4kvn.spigot.homes.usecase.GetTemporaryPlayerHomeUseCase
import com.github.m4kvn.spigot.homes.usecase.TeleportPlayerHomeUseCase
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomePreviousCommand : SubCommand {
    private val getTemporaryPlayerHomeUseCase by inject<GetTemporaryPlayerHomeUseCase>()
    private val teleportPlayerHomeUseCase by inject<TeleportPlayerHomeUseCase>()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        val playerHome = getTemporaryPlayerHomeUseCase(player) ?: return SubCommand.Response.Failed(failedMessage)
        teleportPlayerHomeUseCase(player, playerHome, savePrevious = false)
        return SubCommand.Response.Success
    }

    private val failedMessage = buildString {
        appendMessage(ChatColor.RED) { GET_TEMPORARY_COMMAND_FAILED }
    }
}