package io.snwykks.snowFlake.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Generation(
    val version: String = "1.2",

    @YamlComment("The worlds where Far Lands generation is applied.")
    val worlds: List<String> = listOf("world"),

    @YamlComment("^ENTER Far Lands generation settings.")
    val farlands: FarLandsSettings = FarLandsSettings()
) {
    @Serializable
    data class FarLandsSettings(
        @YamlComment("Distance from world center where Far Lands begin. Original Beta 1.7.3 value: 12550821.")
        val distance: Int = 12550821,

        @YamlComment("Noise coordinate scale.")
        @SerialName("coordinate-scale") val coordinateScale: Double = 684.412,

        @YamlComment("Noise height scale.")
        @SerialName("height-scale") val heightScale: Double = 684.412,

        @YamlComment("Min noise height.")
        @SerialName("min-height-y") val minHeightY: Int = 0,

        @YamlComment("Max noise height.")
        @SerialName("max-height-y") val maxHeightY: Int = 128,

        @YamlComment("^ENTER Void fade — gradual corruption towards the void.")
        @SerialName("void")
        val void: VoidSettings = VoidSettings()
    ) {
        @Serializable
        data class VoidSettings(
            @YamlComment("Distance at which terrain starts to corrupt.")
            @SerialName("start-distance") val startDistance: Int = 12000,

            @YamlComment("Distance at which terrain disappears completely.")
            @SerialName("end-distance") val endDistance: Int = 14000000
        )
    }
}


