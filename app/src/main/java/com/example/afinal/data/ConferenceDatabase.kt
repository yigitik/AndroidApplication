package com.example.afinal.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * ConferenceDatabase - Room Database ana sınıfı
 * Veritabanı versiyonu ve entity'leri tanımlar
 */
@Database(
    entities = [Participant::class],
    version = 1,
    exportSchema = false
)
abstract class ConferenceDatabase : RoomDatabase() {
    
    /**
     * ParticipantDao instance'ını döner
     */
    abstract fun participantDao(): ParticipantDao
    
    companion object {
        @Volatile
        private var INSTANCE: ConferenceDatabase? = null
        
        /**
         * Database singleton instance'ını döner
         * @param context Application context
         * @return ConferenceDatabase instance
         */
        fun getDatabase(context: Context): ConferenceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConferenceDatabase::class.java,
                    "conference_database"
                )
                    .fallbackToDestructiveMigration() // Migration yerine veritabanını yeniden oluşturur
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

