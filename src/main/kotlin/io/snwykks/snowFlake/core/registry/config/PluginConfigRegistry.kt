package io.snwykks.snowFlake.core.registry.config

import io.snwykks.snowFlake.config.SnowFlakeConfig
import io.snwykks.snowFlake.core.context.SnowFlakeContext
import io.snwykks.snowFlake.core.util.FileFacade

internal object PluginConfigRegistry {
    lateinit var config: FileFacade<SnowFlakeConfig>
        private set

    fun init() {
        config = FileFacade(
            "config.yml",
            SnowFlakeContext.getData(),
            SnowFlakeConfig.serializer()
        ) { SnowFlakeConfig() }
    }

    fun reload() {
        config.reload()
    }
}