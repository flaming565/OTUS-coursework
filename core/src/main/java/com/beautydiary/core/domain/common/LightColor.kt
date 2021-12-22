package com.beautydiary.core.domain.common

import android.graphics.Color
import java.util.*

class LightColor : Color() {

    companion object {
        private const val baseColor = WHITE
        private val baseRed = red(baseColor)
        private val baseGreen = green(baseColor)
        private val baseBlue = blue(baseColor)

        fun generate(): Int {
            val rnd = Random()

            val red = (baseRed + rnd.nextInt(256)) / 2
            val green = (baseGreen + rnd.nextInt(256)) / 2
            val blue = (baseBlue + rnd.nextInt(256)) / 2

            return rgb(red, green, blue)
        }
    }
}