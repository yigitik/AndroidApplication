package com.example.afinal.repository

import com.example.afinal.data.Participant
import com.example.afinal.data.ParticipantDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * ParticipantRepository - MVVM mimarisinde Repository katmanı
 * Veri kaynağı (Room Database) ile ViewModel arasında köprü görevi görür
 */
class ParticipantRepository(private val participantDao: ParticipantDao) {
    
    /**
     * Yeni bir katılımcı ekler
     * @param participant Eklenecek katılımcı nesnesi
     */
    suspend fun insertParticipant(participant: Participant) {
        participantDao.insertParticipant(participant)
    }
    
    /**
     * User ID'ye göre katılımcıyı arar ve LiveData olarak döner
     * @param userId Aranacak kullanıcı ID'si
     * @return Bulunan katılımcıyı LiveData olarak döner
     */
    fun getParticipantById(userId: Int) = participantDao.getParticipantById(userId)
    
    /**
     * User ID'ye göre katılımcıyı arar (suspend fonksiyon)
     * @param userId Aranacak kullanıcı ID'si
     * @return Bulunan katılımcıyı döner (null olabilir)
     */
    suspend fun getParticipantByIdSuspend(userId: Int): Participant? {
        return participantDao.getParticipantByIdSuspend(userId)
    }
    
    /**
     * Tüm katılımcıları getirir
     * @return Tüm katılımcıların listesi
     */
    fun getAllParticipants() = participantDao.getAllParticipants()
}

