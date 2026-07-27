package io.snwykks.snowFlake.command.snowflake.subcommand

import io.snwykks.snowFlake.command.common.SnowFlakeManager
import io.snwykks.snowFlake.command.snowflake.SnowFlakeCommand
import io.snwykks.snowFlake.core.registry.config.ConfigRegistry
import io.snwykks.snowFlake.core.registry.config.PluginConfigRegistry
import io.snwykks.snowFlake.messages.SnowFlakeMessage

internal object SFCommandReload {
    val manager = SnowFlakeManager.manager
    val config = PluginConfigRegistry.config.value

    fun register() {
        manager.command(
            manager.commandBuilder(config.commands.commandName)
                .literal("reload")
                .permission(config.commands.commandPerms.reload)
                .handler { ctx ->
                    val sender = ctx.sender()

                    ConfigRegistry.reloadAll()

                    SnowFlakeMessage
                        .builder(sender)
                        .template(config.messageTemplates.multiline)
                        .message(config.messages.reloadMessage)
                        .placeholder("prefix", SnowFlakeCommand.config.prefix)
                        .placeholder("module", "reload")
                        .send()
                }
        )
    }
}