package com.example.ch07.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromTags(tags: List<String>): String = gson.toJson(tags)

    @TypeConverter
    fun toTags(value: String): List<String> =
        if (value.isBlank()) emptyList() else gson.fromJson(value, listType)
}
