package com.st10090542.birdlensapp

import android.content.Context
import androidx.room.*
import java.util.*
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import java.util.Date
import androidx.room.TypeConverters
import android.os.Parcel
import android.os.Parcelable


data class BirdObservation(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val species: String,
    val notes: String,
    val latitude: Double,
    val longitude: Double,
    val date: Date
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as Long?,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(species)
        parcel.writeString(notes)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeLong(date.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BirdObservation> {
        override fun createFromParcel(parcel: Parcel): BirdObservation {
            return BirdObservation(parcel)
        }

        override fun newArray(size: Int): Array<BirdObservation?> {
            return arrayOfNulls(size)
        }
    }
}
class Converters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }
}

@Dao
interface BirdObservationDao {
    @Insert
    suspend fun insertObservation(observation: BirdObservation):Long

    @Query("SELECT * FROM bird_observations")
    suspend fun getAllObservations(): List<BirdObservation>

}

@Database(entities = [BirdObservation::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BirdObservationDatabase : RoomDatabase() {
    abstract fun birdObservationDao(): BirdObservationDao

    companion object {
        @Volatile
        private var INSTANCE: BirdObservationDatabase? = null

        fun getDatabase(context: Context): BirdObservationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirdObservationDatabase::class.java,
                    "bird_observation_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}