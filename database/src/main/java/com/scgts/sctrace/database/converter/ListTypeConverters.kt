package com.scgts.sctrace.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ListTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun stringToStringList(data: String?): List<String> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<String>>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringListToString(someObjects: List<String>): String {
        return gson.toJson(someObjects)
    }
}
