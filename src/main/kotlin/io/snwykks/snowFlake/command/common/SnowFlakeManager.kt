package io.snwykks.snowFlake.command.common

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager

internal object SnowFlakeManager {
    lateinit var manager: LegacyPaperCommandManager<CommandSender>; private set

    fun init(plugin: JavaPlugin) {
        manager = LegacyPaperCommandManager.createNative(
            plugin,
            ExecutionCoordinator.simpleCoordinator()
        )
    }
}