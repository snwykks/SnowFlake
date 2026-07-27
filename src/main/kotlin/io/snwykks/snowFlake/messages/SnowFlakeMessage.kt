package io.snwykks.snowFlake.messages

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SnowFlakeMessage(private val sender: CommandSender) {
    private val audience = sender as? Player ?: Bukkit.getConsoleSender()

    class Builder(private val sender: CommandSender) {
        private val mm = MiniMessage.miniMessage()
        private var template: List<String> = emptyList()
        private var message: List<String> = emptyList()
        private val placeholders = mutableMapOf<String, String>()

        fun template(lines: List<String>): Builder {
            this.template = lines
            return this
        }

        fun message(text: String): Builder {
            this.message = listOf(text)
            return this
        }

        fun message(lines: List<String>): Builder {
            this.message = lines
            return this
        }

        fun placeholder(key: String, value: String): Builder {
            placeholders[key] = value
            return this
        }

        fun placeholders(map: Map<String, String>): Builder {
            placeholders.putAll(map)
            return this
        }

        fun send() {
            if (template.isEmpty()) {
//                PtLogger.debug("TokyoMessage Template is empty")
                return
            }

            if (message.isEmpty()) {
//                PtLogger.debug("TokyoMessage message is empty")
                return
            }

            val processedMessage = message.map { line ->
                var result = line
                for ((key, value) in placeholders) {
                    result = result.replace("%$key%", value)
                }
                result
            }

            val processedTemplate = template.map { line ->
                var result = line
                for ((key, value) in placeholders) {
                    if (key != "message") {
                        result = result.replace("%$key%", value)
                    }
                }
                result
            }

            val formattedMessage = processedMessage.joinToString("\n")
            val header = processedTemplate.firstOrNull() ?: ""
            val prefixLine = processedTemplate.getOrNull(1) ?: ""

            val finalMessage = if (processedMessage.size == 1) {
                header + "\n" + prefixLine.replace("%message%", formattedMessage)
            } else {
                header + "\n" + formattedMessage.split("\n").joinToString("\n") { line ->
                    prefixLine.replace("%message%", line)
                }
            }

            sender.sendMessage(mm.deserialize(finalMessage))
        }
    }

    companion object {
        fun builder(sender: CommandSender): Builder = Builder(sender)
    }
}