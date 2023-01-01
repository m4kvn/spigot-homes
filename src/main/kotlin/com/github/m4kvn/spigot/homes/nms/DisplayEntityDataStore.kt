package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeChunk

interface DisplayEntityDataStore {

    /**
     * 保存されている全てのDisplayEntityを取得する
     */
    fun getDisplayEntities(): List<DisplayEntity>

    /**
     * 指定したホームに対応するDisplayEntityのListを取得する
     */
    fun getDisplayEntities(home: PlayerHome): List<DisplayEntity>

    /**
     * 指定したチャンクに対応するDisplayEntityのListを取得する
     */
    fun getDisplayEntitiesIn(chunk: PlayerHomeChunk): List<DisplayEntity>

    /**
     * 指定したホームに対応するDisplayEntityのListを追加する
     */
    fun addDisplayEntities(home: PlayerHome, entities: List<DisplayEntity>)

    /**
     * 指定したチャンクに対応するDisplayEntityを全て削除する
     *
     * @return 削除したDisplayEntityのListを返し、何も削除しなかった場合は空のListを返す
     */
    fun removeDisplayEntitiesIn(chunk: PlayerHomeChunk): List<DisplayEntity>

    /**
     * 指定したホームに対応するDisplayEntityを全て削除する
     *
     * @return 削除したDisplayEntityのListを返し、何も削除しなかった場合は空のListを返す
     */
    fun removeDisplayEntities(home: PlayerHome): List<DisplayEntity>

    /**
     * 全てのDisplayEntityを削除する
     *
     * @return 削除したDisplayEntityのListを返す
     */
    fun removeAllDisplayEntities(): List<DisplayEntity>
}