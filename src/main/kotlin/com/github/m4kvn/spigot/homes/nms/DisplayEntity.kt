package com.github.m4kvn.spigot.homes.nms

interface DisplayEntity {
    val isAlive: Boolean
    val customText: String
    fun dead()
}