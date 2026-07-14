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
 * RegistrationViewModel - Module A için ViewModel
 * Katılımcı kayıt işlemlerini yönetir ve UI ile veri katmanı arasında köprü görevi görür
 */
class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ParticipantRepository
    
    // Kayıt işleminin başarılı olup olmadığını gösteren LiveData
    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus
    
    init {
        val database = ConferenceDatabase.getDatabase(application)
        repository = ParticipantRepository(database.participantDao())
    }
    
    /**
     * Yeni bir katılımcı kaydeder
     * @param userId Kullanıcı ID'si
     * @param fullName Tam ad
     * @param title Unvan
     * @param registrationType Kayıt tipi (1-Full, 2-Student, 3-None)
     * @param photoPath Profil fotoğrafı yolu
     */
    fun registerParticipant(
        userId: Int,
        fullName: String,
        title: String,
        registrationType: Int,
        photoPath: String?
    ) {
        viewModelScope.launch {
            try {
                // Form validasyonu
                if (fullName.isBlank()) {
                    _registrationStatus.value = RegistrationStatus.Error("Lütfen tam adınızı giriniz")
                    return@launch
                }
                
                if (userId <= 0) {
                    _registrationStatus.value = RegistrationStatus.Error("Geçerli bir kullanıcı ID'si giriniz")
                    return@launch
                }
                
                // Unique ID kontrolü - userId zaten varsa hata göster
                val existingParticipant = repository.getParticipantByIdSuspend(userId)
                if (existingParticipant != null) {
                    _registrationStatus.value = RegistrationStatus.Error("Bu kullanıcı ID'si zaten kullanılıyor. Lütfen farklı bir ID giriniz.")
                    return@launch
                }
                
                // Participant nesnesi oluştur
                val participant = Participant(
                    userId = userId,
                    fullName = fullName,
                    title = title,
                    registrationType = registrationType,
                    photoPath = photoPath
                )
                
                // Veritabanına kaydet
                repository.insertParticipant(participant)
                _registrationStatus.value = RegistrationStatus.Success("Kayıt başarıyla tamamlandı!")
                
            } catch (e: Exception) {
                _registrationStatus.value = RegistrationStatus.Error("Kayıt sırasında bir hata oluştu: ${e.message}")
            }
        }
    }
    
    /**
     * RegistrationStatus - Kayıt işleminin durumunu temsil eden sealed class
     */
    sealed class RegistrationStatus {
        data class Success(val message: String) : RegistrationStatus()
        data class Error(val message: String) : RegistrationStatus()
    }
}

