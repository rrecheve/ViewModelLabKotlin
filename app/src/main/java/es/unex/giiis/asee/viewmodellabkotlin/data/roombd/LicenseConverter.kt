package es.unex.giiis.asee.viewmodellabkotlin.data.roombd

import androidx.room.TypeConverter
import es.unex.giiis.asee.viewmodellabkotlin.data.model.License

class LicenseConverter {
    @TypeConverter
    fun str2License(license: String?): License? {
        return license?.let { License(it) }
    }

    @TypeConverter
    fun license2Str(license: License?): String? {
        return license?.key
    }
}