package io.snwykks.snowFlake.generation.populator

import io.snwykks.snowFlake.config.Generation
import io.snwykks.snowFlake.core.util.SnowLogger
import io.snwykks.snowFlake.generation.NoiseGeneratorOctaves
import org.bukkit.Material
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import java.util.Random

internal class SnowFlakePopulator(private val config: Generation) : BlockPopulator() {

    // region — Far Lands settings from config
    private val distance         : Int    = config.farlands.distance
    private val coordinateScale  : Double = config.farlands.coordinateScale
    private val heightScale      : Double = config.farlands.heightScale
    private val voidStartDistance: Int    = config.farlands.void.startDistance
    private val voidEndDistance  : Int    = config.farlands.void.endDistance
    // endregion

    // region — Noise generators (lazy init per world seed)
    private var minLimitNoise: NoiseGeneratorOctaves? = null
    private var maxLimitNoise: NoiseGeneratorOctaves? = null
    private var mainNoise    : NoiseGeneratorOctaves? = null
    private var depthNoise   : NoiseGeneratorOctaves? = null
    private var scaleNoise   : NoiseGeneratorOctaves? = null
    // endregion

    // region — Reusable noise arrays
    private var noiseArray    : DoubleArray? = null
    private var minLimitArray : DoubleArray? = null
    private var maxLimitArray : DoubleArray? = null
    private var mainNoiseArray: DoubleArray? = null
    private var depthArray    : DoubleArray? = null
    private var scaleArray    : DoubleArray? = null
    // endregion

    override fun populate(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, region: LimitedRegion) {
        // region — World check: only apply to configured worlds
        if (worldInfo.name !in config.worlds) return
        // endregion

        val blockX = chunkX.toLong() * 16L
        val blockZ = chunkZ.toLong() * 16L

        val isFarX = Math.abs(blockX) >= distance.toLong()
        val isFarZ = Math.abs(blockZ) >= distance.toLong()

        if (!isFarX && !isFarZ) return

        initGenerators(worldInfo.seed)

        val noiseX = 5
        val noiseZ = 5
        val noiseY = 33

        noiseArray = initializeNoiseField(noiseArray, chunkX * 4, 0, chunkZ * 4, noiseX, noiseY, noiseZ)

        val worldX = chunkX * 16
        val worldZ = chunkZ * 16

        // region — Clear column: bedrock at bottom, air above
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val gx = worldX + x
                val gz = worldZ + z

                if (worldInfo.minHeight >= region.buffer - 64) {
                    region.setType(gx, worldInfo.minHeight, gz, Material.BEDROCK)
                }

                for (y in worldInfo.minHeight + 1 until worldInfo.maxHeight) {
                    region.setType(gx, y, gz, Material.AIR)
                }
            }
        }
        // endregion

        // region — Fill terrain using trilinear interpolation of noise
        for (x in 0 until 4) {
            for (z in 0 until 4) {
                for (y in 0 until 32) {
                    var d1 = noiseArray!![(( x      * noiseZ + z    ) * noiseY + y    )]
                    var d2 = noiseArray!![(( x      * noiseZ + z + 1) * noiseY + y    )]
                    var d3 = noiseArray!![(((x + 1) * noiseZ + z    ) * noiseY + y    )]
                    var d4 = noiseArray!![(((x + 1) * noiseZ + z + 1) * noiseY + y    )]
                    val d5 = (noiseArray!![(( x      * noiseZ + z    ) * noiseY + y + 1)] - d1) * 0.125
                    val d6 = (noiseArray!![(( x      * noiseZ + z + 1) * noiseY + y + 1)] - d2) * 0.125
                    val d7 = (noiseArray!![(((x + 1) * noiseZ + z    ) * noiseY + y + 1)] - d3) * 0.125
                    val d8 = (noiseArray!![(((x + 1) * noiseZ + z + 1) * noiseY + y + 1)] - d4) * 0.125

                    for (dy in 0 until 8) {
                        var d9  = d1
                        var d10 = d2
                        val d11 = (d3 - d1) * 0.25
                        val d12 = (d4 - d2) * 0.25

                        for (dx in 0 until 4) {
                            var d13 = d9
                            val d14 = (d10 - d9) * 0.25

                            for (dz in 0 until 4) {
                                val bx  = x * 4 + dx
                                val bz  = z * 4 + dz
                                val by  = y * 8 + dy
                                val gbx = worldX + bx
                                val gbz = worldZ + bz

                                if (by < 128 && region.isInRegion(gbx, by, gbz)) {
                                    val axisDist = Math.max(Math.abs(gbx), Math.abs(gbz)).toDouble()

                                    val voidFactor = when {
                                        axisDist > voidStartDistance.toDouble() -> {
                                            ((axisDist - voidStartDistance.toDouble()) / (voidEndDistance - voidStartDistance).toDouble())
                                                .coerceIn(0.0, 1.0)
                                        }
                                        else -> 0.0
                                    }

                                    val density = if (voidFactor > 0.0) d13 - voidFactor * 150.0 else d13

                                    when {
                                        density > 0.0 -> region.setType(gbx, by, gbz, Material.STONE)
                                        by < 63 && axisDist <= voidEndDistance.toDouble() -> {
                                            if (axisDist > voidStartDistance.toDouble()) {
                                                if (voidFactor < 0.2) region.setType(gbx, by, gbz, Material.WATER)
                                            } else {
                                                region.setType(gbx, by, gbz, Material.WATER)
                                            }
                                        }
                                    }
                                }

                                d13 += d14
                            }

                            d9  += d11
                            d10 += d12
                        }

                        d1 += d5
                        d2 += d6
                        d3 += d7
                        d4 += d8
                    }
                }
            }
        }
        // endregion

        // region — Surface pass: STONE → GRASS_BLOCK + DIRT
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val gx = worldX + x
                val gz = worldZ + z
                var surfaceFound = false

                for (y in 127 downTo 0) {
                    if (!region.isInRegion(gx, y, gz)) continue

                    when (region.getType(gx, y, gz)) {
                        Material.STONE -> {
                            if (!surfaceFound) {
                                region.setType(gx, y, gz, Material.GRASS_BLOCK)
                                surfaceFound = true
                            } else {
                                val hasGrassAbove = (1..3).any { offset ->
                                    region.isInRegion(gx, y + offset, gz) &&
                                            region.getType(gx, y + offset, gz) == Material.GRASS_BLOCK
                                }
                                if (hasGrassAbove) region.setType(gx, y, gz, Material.DIRT)
                            }
                        }
                        Material.AIR -> surfaceFound = false
                        else         -> {}
                    }
                }
            }
        }
        // endregion
    }

    // region — Lazy init of noise generators per world seed
    private fun initGenerators(seed: Long) {
        if (minLimitNoise != null) return
        val rand = Random(seed)
        minLimitNoise = NoiseGeneratorOctaves(rand, 16)
        maxLimitNoise = NoiseGeneratorOctaves(rand, 16)
        mainNoise     = NoiseGeneratorOctaves(rand, 8)
        scaleNoise    = NoiseGeneratorOctaves(rand, 10)
        depthNoise    = NoiseGeneratorOctaves(rand, 16)
        SnowLogger.info("<blue>SnowFlake noise generators initialized (seed: $seed)</blue>")
    }
    // endregion

    // region — Noise field initialization (trilinear interpolation source)
    private fun initializeNoiseField(
        array: DoubleArray?,
        x: Int, y: Int, z: Int,
        xSize: Int, ySize: Int, zSize: Int
    ): DoubleArray {
        val result = array ?: DoubleArray(xSize * ySize * zSize)

        scaleArray    = scaleNoise!!.generateNoiseOctaves(scaleArray,     x, z, xSize, zSize, 1.121, 1.121, 0.5)
        depthArray    = depthNoise!!.generateNoiseOctaves(depthArray,     x, z, xSize, zSize, 200.0, 200.0, 0.5)
        mainNoiseArray = mainNoise!!.generateNoiseOctaves(mainNoiseArray, x, y, z, xSize, ySize, zSize, coordinateScale / 80.0, heightScale / 160.0, coordinateScale / 80.0)
        minLimitArray  = minLimitNoise!!.generateNoiseOctaves(minLimitArray, x, y, z, xSize, ySize, zSize, coordinateScale, heightScale, coordinateScale)
        maxLimitArray  = maxLimitNoise!!.generateNoiseOctaves(maxLimitArray, x, y, z, xSize, ySize, zSize, coordinateScale, heightScale, coordinateScale)

        var index  = 0
        var index2 = 0

        for (i in 0 until xSize) {
            for (j in 0 until zSize) {
                var d3 = ((scaleArray!![index2] + 256.0) / 512.0).coerceAtMost(1.0)
                var d4 = depthArray!![index2] / 8000.0
                if (d4 < 0.0) d4 = -d4
                d4 = d4 * 3.0 - 3.0

                if (d4 < 0.0) {
                    d4 /= 2.0
                    if (d4 < -1.0) d4 = -1.0
                    d4 /= 1.4
                    d4 /= 2.0
                    d3 = 0.0
                } else {
                    if (d4 > 1.0) d4 = 1.0
                    d4 /= 6.0
                }

                d3 += 0.5
                d4  = d4 * ySize / 16.0
                val d5 = ySize / 2.0 + d4 * 4.0
                index2++

                for (k in 0 until ySize) {
                    var d7 = ((k.toDouble() - d5) * 12.0) / d3
                    if (d7 < 0.0) d7 *= 4.0

                    val d8  = minLimitArray!![index]  / 512.0
                    val d9  = maxLimitArray!![index]  / 512.0
                    val d10 = (mainNoiseArray!![index] / 10.0 + 1.0) / 2.0

                    var d6 = when {
                        d10 < 0.0 -> d8
                        d10 > 1.0 -> d9
                        else      -> d8 + (d9 - d8) * d10
                    }

                    d6 -= d7

                    if (k > ySize - 4) {
                        val d11 = (k - (ySize - 4)).toDouble() / 3.0
                        d6 = d6 * (1.0 - d11) + (-10.0) * d11
                    }

                    result[index] = d6
                    index++
                }
            }
        }

        return result
    }
    // endregion
}