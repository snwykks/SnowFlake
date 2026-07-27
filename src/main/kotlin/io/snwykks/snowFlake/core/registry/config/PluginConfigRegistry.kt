package io.snwykks.snowFlake.core.registry.config

import io.snwykks.snowFlake.config.Plugin
import io.snwykks.snowFlake.core.context.SnowFlakeContext
import io.snwykks.snowFlake.core.util.FileFacade

internal object PluginConfigRegistry {
    lateinit var config: FileFacade<Plugin>
        private set

    fun init() {
        config = FileFacade(
            "config.yml",
            SnowFlakeContext.getData(),
            Plugin.serializer()
        ) { Plugin() }
    }

    fun reload() {
        config.reload()
    }
}