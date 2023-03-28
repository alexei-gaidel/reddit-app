package ru.gas.humblr.domain.utils

import java.text.SimpleDateFormat
import kotlin.math.ln
import kotlin.math.pow

interface AppUtils {
    fun Long.toDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(this * 1000L)
    }

    fun Double.toDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(this * 1000L)
    }

    fun getFormattedNumber(count: Long): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
    }
}