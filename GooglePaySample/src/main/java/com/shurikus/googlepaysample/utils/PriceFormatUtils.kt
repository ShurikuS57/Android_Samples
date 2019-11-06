package com.shurikus.googlepaysample.utils

class PriceFormatUtils {
    companion object {

        fun priceFormat(price: Double): String {
            val priceStr = price.toString()
            val priceSplit = priceStr.split(".")
            return if (priceSplit[1] == "0") {
                priceSplit[0]
            } else {
                priceStr
            }
        }
    }
}