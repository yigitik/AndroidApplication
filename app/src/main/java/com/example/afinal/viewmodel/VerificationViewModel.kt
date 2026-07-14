package com.example.afinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.afinal.data.ConferenceDatabase
import com.example.afinal.data.Participant
import com.example.afinal.repository.ParticipantRepository
import kotlinx.coroutines.launch

/**
 * VerificationViewModel - Module B için ViewModel
 * Katılımcı doğrulama işlemlerini yönetir
 */
class VerificationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ParticipantRepository
    
    // Doğrulanan katılımcı bilgisini tutan LiveData
    private val _participant = MutableLiveData<Participant?>()
    val participant: LiveData<Participant?> = _participant
    
    // Doğrulama durumunu gösteren LiveData
    private val _verificationStatus = MutableLiveData<VerificationStatus>()
    val verificationStatus: LiveData<VerificationStatus> = _verificationStatus
    
    init {
        val database = ConferenceDatabase.getDatabase(application)
        repository = ParticipantRepository(database.participantDao())
    }
    
    /**
     * User ID'ye göre katılımcıyı arar ve doğrular
     * @param userId Aranacak kullanıcı ID'si
     */
    fun verifyParticipant(userId: Int) {
        viewModelScope.launch {
            try {
                if (userId <= 0) {
                    _verificationStatus.value = VerificationStatus.Error("Geçerli bir kullanıcı ID'si giriniz")
                    _participant.value = null
                    return@launch
                }
                
                val foundParticipant = repository.getParticipantByIdSuspend(userId)
                
                if (foundParticipant == null) {
                    // Kullanıcı bulunamadı
                    _verificationStatus.value = VerificationStatus.NotFound
                    _participant.value = null
                } else {
                    // Kullanıcı bulundu
                    _participant.value = foundParticipant
                    _verificationStatus.value = VerificationStatus.Found(foundParticipant.registrationType)
                }
                
            } catch (e: Exception) {
                _verificationStatus.value = VerificationStatus.Error("Doğrulama sırasında bir hata oluştu: ${e.message}")
                _participant.value = null
            }
        }
    }
    
    /**
     * VerificationStatus - Doğrulama işleminin durumunu temsil eden sealed class
     */
    sealed class VerificationStatus {
        object NotFound : VerificationStatus() // Kullanıcı bulunamadı (Kırmızı arka plan)
        data class Found(val registrationType: Int) : VerificationStatus() // Kullanıcı bulundu (Tip'e göre renk)
        data class Error(val message: String) : VerificationStatus() // Hata durumu
    }
}

