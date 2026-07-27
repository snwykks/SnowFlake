package io.snwykks.snowFlake.core.registry.config

import io.snwykks.snowFlake.core.registry.Registry

object ConfigRegistry: Registry {

    override fun init() {
        PluginConfigRegistry.init()
        GenerationConfigRegistry.init()
    }

    override fun reloadAll() {
        PluginConfigRegistry.reload()
        GenerationConfigRegistry.reload()
    }
}