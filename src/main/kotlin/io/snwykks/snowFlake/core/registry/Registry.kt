package io.snwykks.snowFlake.core.registry

interface Registry {
    fun init()
    fun reloadAll() {}
    fun close() {}
}