package com.github.m4kvn.spigot.playground

import com.github.m4kvn.spigot.playground.nms.CustomArmorStandEntity
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {

    override fun onEnable() {
        val playgroundCommendExecutor = PlaygroundCommendExecutor()
        val playgroundCommand = getCommand("playground")
        playgroundCommand?.setExecutor(playgroundCommendExecutor)
    }
}

class PlaygroundCommendExecutor : CommandExecutor {
    private var customArmorStandEntity: CustomArmorStandEntity? = null

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        if (customArmorStandEntity == null) {
            val newEntity = CustomArmorStandEntity(sender)
            customArmorStandEntity = newEntity
            newEntity.spawn()
        } else {
            customArmorStandEntity?.dead()
            customArmorStandEntity = null
        }
        return true
    }
}