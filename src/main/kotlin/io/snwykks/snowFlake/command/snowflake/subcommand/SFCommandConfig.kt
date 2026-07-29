package io.snwykks.snowFlake.command.snowflake.subcommand

import io.snwykks.snowFlake.command.common.SnowFlakeManager
import io.snwykks.snowFlake.core.registry.config.GenerationConfigRegistry
import io.snwykks.snowFlake.core.registry.config.PluginConfigRegistry
import io.snwykks.snowFlake.messages.SnowFlakeMessage

internal object SFCommandConfig {
    val manager = SnowFlakeManager.manager
    val config = PluginConfigRegistry.config.value

    fun register() {
        manager.command(
            manager.commandBuilder(config.commands.commandName)
                .literal("config")
                .permission(config.commands.commandPerms.config)
                .handler { ctx ->
                    val sender = ctx.sender()

                    val farlands = GenerationConfigRegistry.config.value
                    val worlds = farlands.worlds.joinToString(", ")
                    val distance = farlands.farlands.distance.toString()
                    val coordinateScale = farlands.farlands.coordinateScale.toString()
                    val heightScale = farlands.farlands.heightScale.toString()
                    val minHeightY = farlands.farlands.minHeightY.toString()
                    val maxHeightY = farlands.farlands.maxHeightY.toString()

                    SnowFlakeMessage
                        .builder(sender)
                        .template(config.messageTemplates.multiline)
                        .message(config.messages.configmessage)
                        .placeholder("prefix", config.prefix)
                        .placeholder("module", "generation")
                        .placeholder("worlds", worlds)
                        .placeholder("distance", distance)
                        .placeholder("coordinateScale", coordinateScale)
                        .placeholder("heightScale", heightScale)
                        .placeholder("minHeightY", minHeightY)
                        .placeholder("maxHeightY", maxHeightY)
                        .send()
                }
        )
    }
}