package io.snwykks.snowFlake.generation

import java.util.Random

internal class NoiseGeneratorPerlin(random: Random) {

    private val permutations = IntArray(512)
    val xCoord: Double = random.nextDouble() * 256.0
    val yCoord: Double = random.nextDouble() * 256.0
    val zCoord: Double = random.nextDouble() * 256.0

    init {
        for (i in 0 until 256) permutations[i] = i

        for (i in 0 until 256) {
            val j = random.nextInt(256 - i) + i
            val k = permutations[i]
            permutations[i] = permutations[j]
            permutations[j] = k
            permutations[i + 256] = permutations[i]
        }
    }

    private fun lerp(d: Double, d1: Double, d2: Double): Double = d1 + d * (d2 - d1)

    private fun grad(i: Int, d: Double, d1: Double, d2: Double): Double {
        val j  = i and 15
        val d3 = if (j < 8) d else d1
        val d4 = if (j < 4) d1 else if (j != 12 && j != 14) d2 else d
        return (if ((j and 1) == 0) d3 else -d3) + (if ((j and 2) == 0) d4 else -d4)
    }

    private fun fade(t: Double): Double = t * t * t * (t * (t * 6.0 - 15.0) + 10.0)

    fun populateNoiseArray(
        array: DoubleArray,
        xOffset: Double, yOffset: Double, zOffset: Double,
        xSize: Int, ySize: Int, zSize: Int,
        xScale: Double, yScale: Double, zScale: Double,
        amplitude: Double
    ) {
        var index = 0
        val d = 1.0 / amplitude

        for (x in 0 until xSize) {
            var d6 = (xOffset + x.toDouble()) * xScale + xCoord
            var j1 = d6.toInt()
            if (d6 < j1.toDouble()) j1--
            val k1 = j1 and 255
            d6 -= j1.toDouble()
            val d8 = fade(d6)

            for (z in 0 until zSize) {
                var d9 = (zOffset + z.toDouble()) * zScale + zCoord
                var l1 = d9.toInt()
                if (d9 < l1.toDouble()) l1--
                val i2 = l1 and 255
                d9 -= l1.toDouble()
                val d11 = fade(d9)

                for (y in 0 until ySize) {
                    var d12 = (yOffset + y.toDouble()) * yScale + yCoord
                    var j2 = d12.toInt()
                    if (d12 < j2.toDouble()) j2--
                    val k2 = j2 and 255
                    d12 -= j2.toDouble()
                    val d14 = fade(d12)

                    val A  = permutations[k1]       + i2
                    val AA = permutations[A]        + k2
                    val AB = permutations[A + 1]    + k2
                    val B  = permutations[k1 + 1]   + i2
                    val BA = permutations[B]        + k2
                    val BB = permutations[B + 1]    + k2

                    val lerp1 = lerp(d8, grad(permutations[AA],     d6,        d12,        d9       ), grad(permutations[BA],     d6 - 1.0, d12,        d9       ))
                    val lerp2 = lerp(d8, grad(permutations[AB],     d6,        d12 - 1.0,  d9       ), grad(permutations[BB],     d6 - 1.0, d12 - 1.0,  d9       ))
                    val lerp3 = lerp(d8, grad(permutations[AA + 1], d6,        d12,        d9 - 1.0 ), grad(permutations[BA + 1], d6 - 1.0, d12,        d9 - 1.0 ))
                    val lerp4 = lerp(d8, grad(permutations[AB + 1], d6,        d12 - 1.0,  d9 - 1.0 ), grad(permutations[BB + 1], d6 - 1.0, d12 - 1.0,  d9 - 1.0 ))

                    val value = lerp(d14, lerp(d11, lerp1, lerp3), lerp(d11, lerp2, lerp4))
                    array[index++] += value * d
                }
            }
        }
    }
}