package com.github.m4kvn.spigot.homes.nms

interface DisplayEntity {
    var text: String?
    var location: DisplayEntityLocation
    var isVisible: Boolean
    var isDead: Boolean
}