package io.snwykks.snowFlake.command.snowflake

import io.snwykks.snowFlake.command.common.SnowFlakeManager
import io.snwykks.snowFlake.command.snowflake.subcommand.SFCommandConfig
import io.snwykks.snowFlake.command.snowflake.subcommand.SFCommandReload
import io.snwykks.snowFlake.core.registry.config.PluginConfigRegistry
import io.snwykks.snowFlake.messages.SnowFlakeMessage

internal object SnowFlakeCommand {
    val manager = SnowFlakeManager.manager
    val config = PluginConfigRegistry.config.value

    fun register() {
        manager.command(
            manager.commandBuilder(
                config.commands.commandName,
                *config.commands.commandAliases.toTypedArray()
                )
                .permission(config.commands.commandPerms.dev)
                .handler { ctx ->
                    val sender = ctx.sender()

                    SnowFlakeMessage
                        .builder(sender)
                        .template(config.messageTemplates.multiline)
                        .message(config.messages.helpMessage)
                        .placeholder("prefix", config.prefix)
                        .placeholder("module", "helper")
                        .send()
                }
        )

        SFCommandReload.register()
        SFCommandConfig.register()
    }
}