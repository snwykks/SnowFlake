/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Snwykks.
 */

package io.snwykks.snowFlake.core.registry.command

import io.snwykks.snowFlake.command.snowflake.SnowFlakeCommand
import io.snwykks.snowFlake.core.registry.Registry

object CommandRegistry: Registry {
    override fun init() {
        SnowFlakeCommand.register()
    }
}
