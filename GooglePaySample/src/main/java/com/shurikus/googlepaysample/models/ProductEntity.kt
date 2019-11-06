package com.shurikus.googlepaysample.models

import androidx.annotation.RawRes

data class ProductEntity(
    val id: String,
    val title: String,
    val companyName: String,
    val price: Double,
    val priceWithoutDiscount: Double,
    var count: Int,
    @RawRes val image: Int
)