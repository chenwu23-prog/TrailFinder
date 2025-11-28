package com.example.trailfinder

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return list.joinToString(separator = "|") // safer than comma
    }

    @TypeConverter
    fun fromStringToList(data: String): List<String> {
        return if (data.isBlank()) emptyList() else data.split("|")
    }
}
