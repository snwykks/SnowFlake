package io.snwykks.snowFlake.core.registry.config

import io.snwykks.snowFlake.config.Generation
import io.snwykks.snowFlake.core.context.SnowFlakeContext
import io.snwykks.snowFlake.core.util.FileFacade

internal object GenerationConfigRegistry {
    lateinit var config: FileFacade<Generation>
        private set

    fun init() {
        config = FileFacade(
            "generation.yml",
            SnowFlakeContext.getData(),
            Generation.serializer()
        ) { Generation() }
    }

    fun reload() = config.reload()
}