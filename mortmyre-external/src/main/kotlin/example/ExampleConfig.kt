/*
 * Copyright (c) 2019-2020, ganom <https://github.com/Ganom>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package mortmyre


import meteor.config.legacy.Config
import meteor.config.legacy.ConfigGroup
import meteor.config.legacy.ConfigItem

@ConfigGroup(value = "Examples")
interface ExampleConfig : Config {

    @ConfigItem(
        keyName = "MinimumDelay",
        name = "Minimum Delay(Ticks)",
        description = "How long you wait between casting the next spell(minimum)",
        position = 0,

    )
    fun MinimumDelay(): Int {
        return 1
    }

    @ConfigItem(
        keyName = "MaximumDelay",
        name = "Maximum Delay (Ticks)",
        description = "How long you wait between casting the next spell (max)",
        position = 1,

        )
    fun MaximumDelay(): Int {
        return 5
    }




}