/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Snwykks.
 */

package io.snwykks.snowFlake.core.registry.config

import io.snwykks.snowFlake.core.registry.Registry

object ConfigRegistry: Registry {

    override fun init() {
        PluginConfigRegistry.init()
        GenerationConfigRegistry.init()
    }

    override fun reloadAll() {
        PluginConfigRegistry.reload()
        GenerationConfigRegistry.reload()
    }
}
