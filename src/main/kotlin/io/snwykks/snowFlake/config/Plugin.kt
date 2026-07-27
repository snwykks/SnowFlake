package io.snwykks.snowFlake.config

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Plugin(
    @YamlComment("Don't change it if you don't know what it is.")
    val version: String = "1.1.0",

    @YamlComment("^ENTER prefix for messages in minecraft.")
    val prefix: String = "<gradient:#4490f3:#b3d4ff>SnowFlake</gradient>",

    @SerialName("message_templates") val messageTemplates: MessageTemplates = MessageTemplates(),

    @YamlComment("^ENTER neofetch for plugin fetch on start.")
    val neofetch: List<String> = listOf(
        "<blue>                           __ _       _          </blue>",
        "<blue>                          / _| |     | |         </blue>",
        "<blue>  ___ _ __   _____      _| |_| | __ _| | _____   </blue>",
        "<blue> / __| '_ \\ / _ \\ \\ /\\ / /  _| |/ _` | |/ / _ \\ </blue>",
        "<blue> \\__ \\ | | | (_) \\ V  V /| | | | (_| |   <  __/ </blue>",
        "<blue> |___/_| |_|\\___/ \\_/\\_/ |_| |_|\\__,_|_|\\_\\___| </blue>"
    ),

    @YamlComment("^ENTER Command in plugin with permissions.")
    val commands: Commands = Commands(),

    @YamlComment("^ENTER Plugin command messages.")
    val messages: Messages = Messages()
)

@Serializable
data class MessageTemplates(
    val simple: List<String> = listOf(
        "<white>❏ <bold>%prefix%</white>  <bold>%module%</bold> <bold><#7fc4f5>*</#7fc4f5></bold> %message%"
    ),
    val multiline: List<String> = listOf(
        "<white>❏ <bold>%prefix%</white>  <bold>%module%</bold>",
        "<bold><#7fc4f5>*</#7fc4f5></bold> <white>%message%</white>"
    ),
    val advanced: List<String> = listOf(
        "<white>❏ <bold>%prefix%</white>  <bold>%module%</bold>",
        "<bold><#7fc4f5>*</#7fc4f5></bold> <white>%message%</white>",
        "<#a57ff5>+</#a57ff5> <white>%inspector%</white>"
    )
)

@Serializable
data class Commands(
    @SerialName("command") val commandName: String = "snowflake",
    @SerialName("aliases") val commandAliases: List<String> = listOf("sf"),
    @SerialName("permissions") val commandPerms: CommandPerms = CommandPerms()
)

@Serializable
data class CommandPerms(
    @SerialName("config")   val config: String = "snowflake.config",
    @SerialName("reload")   val reload: String = "snowflake.reload",
    @SerialName("dev_perm") val dev: String = "snowflake.dev"
)

@Serializable
data class Messages (
    @SerialName("help_message") val helpMessage: List<String> = listOf(
        "<white>Использование: /sf {argument}</white>",
        "<#7fc4f5>Подкоманды:</#7fc4f5>",
        "<white>/sf reload <gray>-</gray> Перезагрузка конфигов.</white>",
        "<white>/sf config <gray>-</gray> Вывод настроек генерации.</white>",
    ),

    @SerialName("reload_message") val reloadMessage: String = "<white>Config has reloaded successfully!</white>",

    @SerialName("config_message") val configmessage: List<String> = listOf(
        "<white>Active worlds: <#7fc4f5>%worlds%</#7fc4f5><white>",
        "<white>Distance: <#7fc4f5>%distance%</#7fc4f5><white>",
        "<white>CoordinateScale: <#7fc4f5>%coordinateScale%</#7fc4f5><white>",
        "<white>HeightScale: <#7fc4f5>%heightScale%</#7fc4f5><white>",
        "<white>minHeightY: <#7fc4f5>%minHeightY%</#7fc4f5></white>",
        "<white>maxHeightY: <#7fc4f5>%maxHeightY%</#7fc4f5></white>"
    )
)
