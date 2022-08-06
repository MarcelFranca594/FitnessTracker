package co.tiagoaguiar.fitnesstracker.model

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

object DateConverter {

    @TypeConverter
    fun toDate(dateLong: Long?) : Date? { // Buscar um objeto
        return if (dateLong != null) Date(dateLong) else null

    }

    @TypeConverter
    fun fromDate(date: Date?): Long? { // Gravar no banco de dados
        return date?.time
    }
}