/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Snwykks.
 */

package io.snwykks.snowFlake.core.registry.config

import io.snwykks.snowFlake.config.Generation
import io.snwykks.snowFlake.core.context.SnowFlakeContext
import io.snwykks.snowFlake.core.util.FileFacade

internal object GenerationConfigRegistry {
    lateinit var config: FileFacade<Generation>
        private set

    fun init() {
        config = FileFacade(
            "generation.yml",
            SnowFlakeContext.getData(),
            Generation.serializer()
        ) { Generation() }
    }

    fun reload() = config.reload()
}
