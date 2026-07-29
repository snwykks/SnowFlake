package io.snwykks.snowFlake.generation.populator

import io.snwykks.snowFlake.config.Generation
import io.snwykks.snowFlake.core.util.SnowLogger
import io.snwykks.snowFlake.generation.NoiseGeneratorOctaves
import org.bukkit.Material
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

internal class SnowFlakePopulator(private val config: Generation) : BlockPopulator() {

    private val distance: Int = config.farlands.distance
    private val coordinateScale: Double = config.farlands.coordinateScale
    private val heightScale: Double = config.farlands.heightScale
    private val minHeightY: Int = config.farlands.minHeightY
    private val maxHeightY: Int = config.farlands.maxHeightY
    private val voidStartDistance: Int = config.farlands.void.startDistance
    private val voidEndDistance: Int = config.farlands.void.endDistance

    private val noiseCache = ConcurrentHashMap<Long, NoiseGenerators>()

    private data class NoiseGenerators(
        val minLimitNoise: NoiseGeneratorOctaves,
        val maxLimitNoise: NoiseGeneratorOctaves,
        val mainNoise: NoiseGeneratorOctaves,
        val depthNoise: NoiseGeneratorOctaves,
        val scaleNoise: NoiseGeneratorOctaves
    )

    override fun populate(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, region: LimitedRegion) {
        if (worldInfo.name !in config.worlds) return

        val blockX = chunkX.toLong() * 16L
        val blockZ = chunkZ.toLong() * 16L

        val isFarX = abs(blockX) >= distance.toLong()
        val isFarZ = abs(blockZ) >= distance.toLong()

        if (!isFarX && !isFarZ) return

        val generators = noiseCache.getOrPut(worldInfo.seed) {
            val rand = Random(worldInfo.seed)
            NoiseGenerators(
                minLimitNoise = NoiseGeneratorOctaves(rand, 16),
                maxLimitNoise = NoiseGeneratorOctaves(rand, 16),
                mainNoise     = NoiseGeneratorOctaves(rand, 8),
                scaleNoise    = NoiseGeneratorOctaves(rand, 10),
                depthNoise    = NoiseGeneratorOctaves(rand, 16)
            ).also {
                SnowLogger.info("<blue>SnowFlake noise generators initialized (seed: ${worldInfo.seed})</blue>")
            }
        }

        val noiseX = 5
        val noiseZ = 5
        val noiseY = (maxHeightY - minHeightY) / 8 + 1

        val noiseArray = initializeNoiseField(
            generators,
            chunkX * 4,
            minHeightY / 4,
            chunkZ * 4,
            noiseX, noiseY, noiseZ
        )

        val worldX = chunkX * 16
        val worldZ = chunkZ * 16

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val gx = worldX + x
                val gz = worldZ + z

                if (region.isInRegion(gx, worldInfo.minHeight, gz)) {
                    region.setType(gx, worldInfo.minHeight, gz, Material.BEDROCK)
                }

                for (y in worldInfo.minHeight + 1 until worldInfo.maxHeight) {
                    if (!region.isInRegion(gx, y, gz)) continue
                    region.setType(gx, y, gz, Material.AIR)
                }
            }
        }

        for (x in 0 until 4) {
            for (z in 0 until 4) {
                for (y in 0 until 31) {
                    val idx1 = ((x      * noiseZ + z    ) * noiseY + y    )
                    val idx2 = ((x      * noiseZ + z + 1) * noiseY + y    )
                    val idx3 = (((x + 1) * noiseZ + z    ) * noiseY + y    )
                    val idx4 = (((x + 1) * noiseZ + z + 1) * noiseY + y    )
                    val idx5 = ((x      * noiseZ + z    ) * noiseY + y + 1)
                    val idx6 = ((x      * noiseZ + z + 1) * noiseY + y + 1)
                    val idx7 = (((x + 1) * noiseZ + z    ) * noiseY + y + 1)
                    val idx8 = (((x + 1) * noiseZ + z + 1) * noiseY + y + 1)

                    var d1 = safeGet(noiseArray, idx1)
                    var d2 = safeGet(noiseArray, idx2)
                    var d3 = safeGet(noiseArray, idx3)
                    var d4 = safeGet(noiseArray, idx4)
                    val d5 = (safeGet(noiseArray, idx5) - d1) * 0.125
                    val d6 = (safeGet(noiseArray, idx6) - d2) * 0.125
                    val d7 = (safeGet(noiseArray, idx7) - d3) * 0.125
                    val d8 = (safeGet(noiseArray, idx8) - d4) * 0.125

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

                                if (by >= maxHeightY || !region.isInRegion(gbx, by, gbz)) {
                                    d13 += d14
                                    continue
                                }

                                val axisDist = maxOf(abs(gbx), abs(gbz)).toDouble()

                                val voidFactor = when {
                                    axisDist > voidStartDistance.toDouble() -> {
                                        ((axisDist - voidStartDistance.toDouble()) /
                                                (voidEndDistance - voidStartDistance).toDouble())
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

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val gx = worldX + x
                val gz = worldZ + z
                var surfaceFound = false

                for (y in maxHeightY - 1 downTo minHeightY) {
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
                        else -> {}
                    }
                }
            }
        }
    }

    private fun safeGet(array: DoubleArray, index: Int): Double {
        return if (index in array.indices) array[index] else 0.0
    }

    private fun initializeNoiseField(
        gen: NoiseGenerators,
        x: Int, y: Int, z: Int,
        xSize: Int, ySize: Int, zSize: Int
    ): DoubleArray {
        val result = DoubleArray(xSize * ySize * zSize)

        val scaleArray = gen.scaleNoise.generateNoiseOctaves(
            null, x, z, xSize, zSize, 1.121, 1.121, 0.5
        )
        val depthArray = gen.depthNoise.generateNoiseOctaves(
            null, x, z, xSize, zSize, 200.0, 200.0, 0.5
        )
        val mainNoiseArray = gen.mainNoise.generateNoiseOctaves(
            null, x, y, z, xSize, ySize, zSize,
            coordinateScale / 80.0, heightScale / 160.0, coordinateScale / 80.0
        )
        val minLimitArray = gen.minLimitNoise.generateNoiseOctaves(
            null, x, y, z, xSize, ySize, zSize,
            coordinateScale, heightScale, coordinateScale
        )
        val maxLimitArray = gen.maxLimitNoise.generateNoiseOctaves(
            null, x, y, z, xSize, ySize, zSize,
            coordinateScale, heightScale, coordinateScale
        )

        var index = 0
        var depthIndex = 0

        for (i in 0 until xSize) {
            for (j in 0 until zSize) {
                var d3 = ((scaleArray[depthIndex] + 256.0) / 512.0).coerceAtMost(1.0)
                var d4 = depthArray[depthIndex] / 8000.0
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
                d4 = d4 * ySize / 16.0
                val d5 = ySize / 2.0 + d4 * 4.0
                depthIndex++

                for (k in 0 until ySize) {
                    var d7 = ((k.toDouble() - d5) * 12.0) / d3
                    if (d7 < 0.0) d7 *= 4.0

                    val d8  = minLimitArray[index] / 512.0
                    val d9  = maxLimitArray[index] / 512.0
                    val d10 = (mainNoiseArray[index] / 10.0 + 1.0) / 2.0

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
}