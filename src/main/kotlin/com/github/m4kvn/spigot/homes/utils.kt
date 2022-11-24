package com.github.m4kvn.spigot.homes

import org.bukkit.ChatColor

inline fun color(
    color: ChatColor,
    string: () -> String,
) = "${color}${string()}${ChatColor.RESET}"