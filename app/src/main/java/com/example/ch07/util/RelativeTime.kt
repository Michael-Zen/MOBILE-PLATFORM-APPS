package com.example.ch07.util

fun formatRelativeTime(now: Long, then: Long): String {
    val diff = (now - then).coerceAtLeast(0L)
    val minute = 60_000L
    val hour = 60L * minute
    val day = 24L * hour
    val month = 30L * day
    val year = 365L * day

    return when {
        diff < minute -> "baru saja"
        diff < hour -> "${diff / minute} menit lalu"
        diff < day -> "${diff / hour} jam lalu"
        diff < month -> "${diff / day} hari lalu"
        diff < year -> "${diff / month} bulan lalu"
        else -> "${diff / year} tahun lalu"
    }
}
