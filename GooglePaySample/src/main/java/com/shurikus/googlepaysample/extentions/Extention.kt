package com.shurikus.googlepaysample.extentions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.round

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

inline fun <reified T> Gson.fromJsonAuto(json: String): T = this.fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.toJsonAuto(data: Any): String = this.toJson(data, object : TypeToken<T>() {}.type)