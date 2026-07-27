package io.snwykks.snowFlake.core.registry.command

import io.snwykks.snowFlake.command.snowflake.SnowFlakeCommand
import io.snwykks.snowFlake.core.registry.Registry

object CommandRegistry: Registry {
    override fun init() {
        SnowFlakeCommand.register()
    }
}