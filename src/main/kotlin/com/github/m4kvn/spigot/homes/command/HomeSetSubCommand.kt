package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.usecase.SetDefaultPlayerHomeUseCase
import com.github.m4kvn.spigot.homes.usecase.SetNamedPlayerHomeUseCase
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomeSetSubCommand : SubCommand {
    private val setDefaultPlayerHomeUseCase by inject<SetDefaultPlayerHomeUseCase>()
    private val setNamedPlayerHomeUseCase by inject<SetNamedPlayerHomeUseCase>()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        if (args.isNotEmpty())
            setNamedPlayerHomeUseCase(player, args[0]) else
            setDefaultPlayerHomeUseCase(player)
        return SubCommand.Response.Success
    }
}