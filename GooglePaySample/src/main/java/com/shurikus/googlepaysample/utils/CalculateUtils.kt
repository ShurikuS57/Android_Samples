package com.shurikus.googlepaysample.utils

import com.shurikus.googlepaysample.models.ProductEntity

class CalculateUtils {
    companion object {

        fun calculatePrice(products: List<ProductEntity>): Double {
            var price = 0.0
            products.forEach {
                price += it.price * it.count
            }
            return price
        }
    }
}