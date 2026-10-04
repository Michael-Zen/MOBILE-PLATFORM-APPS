package com.example.ch07.util

fun parseTags(text: String): List<String> =
    text.split(",")
        .map { it.trim().removePrefix("#").trim().lowercase() }
        .filter { it.isNotEmpty() }
        .distinct()

fun formatTags(tags: List<String>): String =
    tags.joinToString(", ")
