/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Snwykks.
 */

package io.snwykks.snowFlake

import io.snwykks.snowFlake.command.common.SnowFlakeManager
import io.snwykks.snowFlake.core.context.SnowFlakeContext
import io.snwykks.snowFlake.core.registry.command.CommandRegistry
import io.snwykks.snowFlake.core.registry.config.ConfigRegistry
import io.snwykks.snowFlake.core.util.SnowLogger
import io.snwykks.snowFlake.core.util.neofetch.NeoFetch
import io.snwykks.snowFlake.generation.listener.WorldInitListener
import org.bukkit.plugin.java.JavaPlugin

class SnowFlake : JavaPlugin() {
    override fun onEnable() {
        SnowFlakeContext.init(this@SnowFlake)
        SnowFlakeManager.init(this@SnowFlake)

        SnowFlakeContext.register(ConfigRegistry)

        /** Initializing a plugin logger */
        SnowLogger.init()

        /** Registering a WorldInit Listener. */
        SnowFlakeContext.getServer().pluginManager.registerEvents(WorldInitListener, this@SnowFlake)

        SnowFlakeContext.register(CommandRegistry)

        NeoFetch.display()
    }

    override fun onDisable() {
        SnowFlakeContext.closeAll()
        SnowLogger.info("<blue>Goodbye world from SnowFlake ~</blue>")
    }
}
