/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Snwykks.
 */

package io.snwykks.snowFlake.generation.listener

import io.snwykks.snowFlake.core.registry.config.GenerationConfigRegistry
import io.snwykks.snowFlake.core.util.SnowLogger
import io.snwykks.snowFlake.generation.populator.SnowFlakePopulator
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent

internal object WorldInitListener : Listener {
    @EventHandler
    fun onWorldInit(event: WorldInitEvent) {
        val config = GenerationConfigRegistry.config.value

        if (event.world.name !in config.worlds) return

        if (event.world.populators.any { it is SnowFlakePopulator }) return

        event.world.populators.add(SnowFlakePopulator(config))

        SnowLogger.info("<blue>SnowFlake populator attached to world:</blue> <white>${event.world.name}</white>")
    }
}
