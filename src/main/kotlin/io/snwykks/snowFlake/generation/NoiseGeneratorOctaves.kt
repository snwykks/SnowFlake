package io.snwykks.snowFlake.generation

internal class NoiseGeneratorOctaves(random: java.util.Random, private val octaves: Int) {

    private val generatorCollection = Array(octaves) { NoiseGeneratorPerlin(random) }

    fun generateNoiseOctaves(
        array: DoubleArray?,
        x: Int, z: Int,
        sizeX: Int, sizeZ: Int,
        scaleX: Double, scaleZ: Double, scaleY: Double
    ): DoubleArray = generateNoiseOctaves(array, x, 10, z, sizeX, 1, sizeZ, scaleX, 1.0, scaleZ)

    fun generateNoiseOctaves(
        array: DoubleArray?,
        x: Int, y: Int, z: Int,
        sizeX: Int, sizeY: Int, sizeZ: Int,
        scaleX: Double, scaleY: Double, scaleZ: Double
    ): DoubleArray {
        val result = if (array == null) {
            DoubleArray(sizeX * sizeY * sizeZ)
        } else {
            array.fill(0.0)
            array
        }

        var d = 1.0
        for (i in 0 until octaves) {
            generatorCollection[i].populateNoiseArray(
                result,
                x.toDouble(), y.toDouble(), z.toDouble(),
                sizeX, sizeY, sizeZ,
                scaleX * d, scaleY * d, scaleZ * d,
                d
            )
            d /= 2.0
        }

        return result
    }
}