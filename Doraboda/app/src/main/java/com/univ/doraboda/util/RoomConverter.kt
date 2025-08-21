package com.univ.doraboda.util

import androidx.room.TypeConverter
import java.util.Date

class RoomConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}