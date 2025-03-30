package com.twoonethree.kalagatodemo.utils

import java.util.Date
import java.util.Locale

fun Date.toFormattedString(): String {
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(this)
}

fun calculateProgress(dueTimeMillis: Long): Float {
    val currentTimeMillis = System.currentTimeMillis()
    val totalDurationMillis = dueTimeMillis - currentTimeMillis
    val maxDurationMillis = 7 * 24 * 60 * 60 * 1000L // 7 days as a reference period

    return if (totalDurationMillis <= 0) 1f else (1f - totalDurationMillis / maxDurationMillis.toFloat()).coerceIn(
        0f,
        1f
    )
}

fun getRemainingHours(dueTimeMillis: Long): Long {
    val currentTimeMillis = System.currentTimeMillis()
    return if (dueTimeMillis <= currentTimeMillis) 0 else (dueTimeMillis - currentTimeMillis) / (1000 * 60 * 60)
}


