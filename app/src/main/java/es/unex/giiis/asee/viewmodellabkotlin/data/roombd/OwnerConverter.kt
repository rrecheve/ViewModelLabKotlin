package es.unex.giiis.asee.viewmodellabkotlin.data.roombd

import androidx.room.TypeConverter
import es.unex.giiis.asee.viewmodellabkotlin.data.model.Owner

class OwnerConverter {
    @TypeConverter
    fun OwnerToUsername(owner: Owner?): String? {
        return owner?.login?.lowercase()
    }

    @TypeConverter
    fun UsernameToOwner(username: String?): Owner? {
        return username?.let { Owner(it) }
    }
}