package pt.ipca.projetopdm.UserInterface.productsList

import androidx.room.TypeConverter
import java.util.Date

class ConverterDate {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}