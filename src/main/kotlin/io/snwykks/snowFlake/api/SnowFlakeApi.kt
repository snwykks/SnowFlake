package io.snwykks.snowFlake.api

import io.snwykks.snowFlake.config.Generation
import io.snwykks.snowFlake.config.SnowFlakeConfig
import io.snwykks.snowFlake.core.registry.config.GenerationConfigRegistry
import io.snwykks.snowFlake.core.registry.config.PluginConfigRegistry
import io.snwykks.snowFlake.core.util.SnowLogger
import io.snwykks.snowFlake.messages.SnowFlakeMessage
import org.bukkit.command.CommandSender

object SnowFlakeApi {
    /** Get plugin config values from API.
     * @since 1.2
     * */
    val pluginConfig: SnowFlakeConfig
        get() = PluginConfigRegistry.config.value

    /** Get generation config values from API.
     * @since 1.2
     * */
    val generationConfig: Generation
        get() = GenerationConfigRegistry.config.value

    /** Get plugin version. */
    val version: String
        get() = pluginConfig.version

    /** Get plugin prefix. */
    val prefix: String
        get() = pluginConfig.prefix

    /** Check world for FarLands modification.
     * @since 1.2
     * */
    fun isFarLandsWorld(worldName: String): Boolean =
        worldName in generationConfig.worlds

    /** Check unique Chunk for FarLands modification.
     * @since 1.2
     * */
    fun isFarLandsChunk(worldName: String, chunkX: Int, chunkZ: Int): Boolean {
        if (worldName !in generationConfig.worlds) return false
        val dist = generationConfig.farlands.distance.toLong()
        val blockX = chunkX.toLong() * 16L
        val blockZ = chunkZ.toLong() * 16L
        return kotlin.math.abs(blockX) >= dist || kotlin.math.abs(blockZ) >= dist
    }

    /** Get SnowFlakeMessage module.
     * @since 1.2
     * */
    fun messageSend(sender: CommandSender): SnowFlakeMessage.Builder =
        SnowFlakeMessage.builder(sender)

    /** Get SnowFlake Plugin Component Logger.
     * @since 1.2
     * */
    val logger: SnowLogger
        get() = SnowLogger
}