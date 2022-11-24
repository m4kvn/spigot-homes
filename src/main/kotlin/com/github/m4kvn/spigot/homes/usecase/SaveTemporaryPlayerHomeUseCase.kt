package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.TemporaryPlayerHomeManager

class SaveTemporaryPlayerHomeUseCase(
    private val temporaryPlayerHomeManager: TemporaryPlayerHomeManager,
) : UseCase {

    operator fun invoke(playerHome: PlayerHome.Temporary) {
        temporaryPlayerHomeManager.add(playerHome)
    }
}