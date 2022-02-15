package com.jcarrasco96.une2021.utils

object Utils {

    private val TRAMO = intArrayOf(
        0,
        100,
        150,
        200,
        250,
        300,
        350,
        400,
        450,
        500,
        600,
        700,
        1000,
        1800,
        2600,
        3400,
        4200,
        5000
    )

    val PRECIO = doubleArrayOf(
        0.33,
        1.07,
        1.43,
        2.46,
        3.00,
        4.00,
        5.00,
        6.00,
        7.00,
        9.20,
        9.45,
        9.85,
        10.80,
        11.80,
        12.90,
        13.95,
        15.00,
        20.00
    )

    private val COSTO_HASTA_FIN_TRAMO = doubleArrayOf(
        0.00,
        33.00,
        86.50,
        158.00,
        281.00,
        431.00,
        631.00,
        881.00,
        1181.00,
        1531.00,
        2451.00,
        3396.00,
        6351.00,
        14991.00,
        24431.00,
        34751.00,
        45911.00,
        57911.00
    )

    fun kWhXTramos(consumo: Long): LongArray {
        val consumoxtramo = longArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var consumoAux = consumo

        for (i in consumoxtramo.size - 1 downTo 0) {
            if (consumoAux > TRAMO[i]) {
                consumoxtramo[i] = consumoAux - TRAMO[i]
                consumoAux -= consumoxtramo[i]
            }
        }

        return consumoxtramo
    }

    fun calcularImporte(kWhXTramos: LongArray): Double {
        var precioTotal = 0.0

        for (i in kWhXTramos.size - 1 downTo 0) {
            precioTotal += kWhXTramos[i] * PRECIO[i]
        }

        return precioTotal
    }

    fun importe(consumo: Long): Double {
        val kWhXTramos = kWhXTramos(consumo)
        return calcularImporte(kWhXTramos)
    }

    fun canPay(dineroqtengo: Double): Int {
        var index = 0

        for (indice in COSTO_HASTA_FIN_TRAMO.indices) {
            val costoshastatramos = COSTO_HASTA_FIN_TRAMO[indice]

            if (costoshastatramos > dineroqtengo) {
                index = indice
                break
            }
        }

        return when (index != 0) {
            true -> {
                val firstIndex = TRAMO[index - 1]
                val secondIndex = TRAMO[index]

                var temp = firstIndex

                for (i in firstIndex..secondIndex) {
                    val importe = importe(i.toLong())

                    if (dineroqtengo >= importe) {
                        temp = i
                    }
                }

                temp
            }
            false -> -1
        }
    }

}