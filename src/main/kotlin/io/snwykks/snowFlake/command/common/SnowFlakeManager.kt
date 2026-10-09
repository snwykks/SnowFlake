/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Snwykks.
 */

package io.snwykks.snowFlake.command.common

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager

internal object SnowFlakeManager {
    lateinit var manager: LegacyPaperCommandManager<CommandSender>; private set

    fun init(plugin: JavaPlugin) {
        manager = LegacyPaperCommandManager.createNative(
            plugin,
            ExecutionCoordinator.simpleCoordinator()
        )
    }
}
