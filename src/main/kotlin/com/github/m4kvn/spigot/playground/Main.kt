package com.github.m4kvn.spigot.playground

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {

    override fun onEnable() {
        val playgroundCommendExecutor = PlaygroundCommendExecutor(this)
        val playgroundCommand = getCommand("playground")
        playgroundCommand?.setExecutor(playgroundCommendExecutor)
    }
}

class PlaygroundCommendExecutor(
    private val plugin: JavaPlugin,
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        sender.sendMessage("command ok")
        return true
    }
}