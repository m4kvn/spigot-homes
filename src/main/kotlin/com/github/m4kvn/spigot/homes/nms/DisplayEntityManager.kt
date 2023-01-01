package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.Chunk
import org.bukkit.World

interface DisplayEntityManager {

    /**
     * 指定した全てのホームのDisplayEntityをスポーンさせる
     */
    fun spawnEntities(chunk: Chunk, playerHomeList: List<PlayerHome>)

    /**
     * 指定したホームのDisplayEntityをスポーンさせる
     */
    fun spawnEntities(world: World, playerHome: PlayerHome)

    /**
     * 指定したチャンクに対応する全てのDisplayEntityを消す
     */
    fun despawnEntities(chunk: Chunk)

    /**
     * 全てのホームのDisplayEntityを消す
     */
    fun despawnAllEntities()

    /**
     * 指定したホームのDisplayEntityを消す
     */
    fun despawnEntities(playerHome: PlayerHome)

    /**
     * 指定したホームのDisplayEntityを生成する
     *
     * @return [PlayerHome]が[PlayerHome.Temporary]の場合は空のListを返す
     */
    fun createEntities(world: World, playerHome: PlayerHome): List<DisplayEntity>
}