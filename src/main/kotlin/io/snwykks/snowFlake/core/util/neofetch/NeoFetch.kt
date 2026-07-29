package io.snwykks.snowFlake.core.util.neofetch

import io.snwykks.snowFlake.config.SnowFlakeConfig
import io.snwykks.snowFlake.core.registry.config.PluginConfigRegistry
import io.snwykks.snowFlake.core.util.SnowLogger

internal object NeoFetch {
    val logger: SnowLogger = SnowLogger

    private val config: SnowFlakeConfig by lazy { PluginConfigRegistry.config.value }

    private val lines: List<String> by lazy { config.neofetch }
    private val version: String     by lazy { config.version }

    fun display() {
        if (lines.isEmpty()) return

        try {
            logger.neofetch(lines, version)
        } catch (e: Exception) {
            logger.error("An error corrupt in neofetch logger sending.")
        }
    }
}