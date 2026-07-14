package com.example.afinal.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * ParticipantDao - Room Database için Data Access Object interface
 * Veritabanı CRUD işlemlerini tanımlar
 */
@Dao
interface ParticipantDao {
    
    /**
     * Yeni bir katılımcı ekler
     * @param participant Eklenecek katılımcı nesnesi
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: Participant)
    
    /**
     * User ID'ye göre katılımcıyı arar
     * @param userId Aranacak kullanıcı ID'si
     * @return Bulunan katılımcıyı LiveData olarak döner (null olabilir)
     */
    @Query("SELECT * FROM participants WHERE userId = :userId")
    fun getParticipantById(userId: Int): LiveData<Participant?>
    
    /**
     * User ID'ye göre katılımcıyı arar (suspend fonksiyon)
     * @param userId Aranacak kullanıcı ID'si
     * @return Bulunan katılımcıyı döner (null olabilir)
     */
    @Query("SELECT * FROM participants WHERE userId = :userId")
    suspend fun getParticipantByIdSuspend(userId: Int): Participant?
    
    /**
     * Tüm katılımcıları getirir
     * @return Tüm katılımcıların listesi
     */
    @Query("SELECT * FROM participants")
    fun getAllParticipants(): LiveData<List<Participant>>
}

