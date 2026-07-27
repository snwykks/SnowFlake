package io.snwykks.snowFlake.core.context

import io.snwykks.snowFlake.core.registry.Registry
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Logger

internal object SnowFlakeContext {
    private lateinit var instance: JavaPlugin
    private val registries = mutableMapOf<Class<out Registry>, Registry>()

    fun init(plugin: JavaPlugin) { instance = plugin }

    fun get()       : JavaPlugin { return instance }
    fun getLogger() : Logger { return instance.logger }
    fun getData()   : File   { return instance.dataFolder }
    fun getServer() : Server { return instance.server }

    fun register(registry: Registry) {
        val clazz = registry::class.java
        registries.getOrPut(clazz) {
            registry.init()
            registry
        }
    }

    inline fun <reified T : Registry> get(): T { return registries[T::class.java] as? T ?: error("Registry ${T::class.simpleName} not registered!") }

    fun closeAll() {
        registries.values.forEach { it.close() }
        registries.clear()
    }
}