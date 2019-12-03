package com.shurikus.googlepaysample.utils

import com.shurikus.googlepaysample.extentions.round

class PriceFormatUtils {
    companion object {

        fun priceFormat(price: Double): String {
            val roundPrice = price.round(2)
            val priceStr = roundPrice.toString()
            val priceSplit = priceStr.split(".")
            return if (priceSplit[1] == "0") {
                priceSplit[0]
            } else {
                priceStr
            }
        }
    }
}