package io.snwykks.snowFlake.core.util

import io.snwykks.snowFlake.core.context.SnowFlakeContext
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.kyori.adventure.text.minimessage.MiniMessage

object SnowLogger {
    private lateinit var logger: ComponentLogger
    private val mm: MiniMessage by lazy { MiniMessage.miniMessage() }

    /**
     * Param for debug mode. Change mode with
     * ` PtLogger.setDebugMode(boolean) `
     * @since 1.0.0
     * */
    private var debugMode: Boolean = false

    fun init() {
        this.logger = SnowFlakeContext.get().componentLogger
    }

    private fun checkInit() {
        if (!::logger.isInitialized) {
            throw IllegalStateException("SnowLogger not initialized!")
        }
    }

    fun setDebugMode(mode: Boolean) { this.debugMode = mode }

    fun info(message: String) {
        checkInit()
        logger.info(mm.deserialize(message))
    }

    fun warn(message: String) {
        checkInit()
        logger.warn(mm.deserialize(message))
    }

    fun error(message: String) {
        checkInit()
        logger.error(mm.deserialize(message))
    }

    fun error(throwable: Throwable, message: String = "An error corrupted") {
        logger.error(mm.deserialize("$message : ${throwable.message}"), throwable)
    }

    fun debug(message: String) {
        if (debugMode) {
            checkInit()
            logger.info(mm.deserialize("[DEBUG] $message"))
        }
    }

    fun neofetch(lines: List<String>, version: String) {
        lines.forEach { line ->
            logger.info(mm.deserialize(line))
        }
    }
}
