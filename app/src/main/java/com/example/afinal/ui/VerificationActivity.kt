package com.example.afinal.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.example.afinal.databinding.ActivityVerificationBinding
import com.example.afinal.ui.RegistrationActivity
import com.example.afinal.viewmodel.VerificationViewModel

/**
 * VerificationActivity - Module B: Katılımcı Doğrulama Ekranı
 * Doğrulama masası simülasyonu - User ID ile katılımcı arama ve doğrulama
 */
class VerificationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVerificationBinding
    private lateinit var viewModel: VerificationViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // ViewModel'i başlat
        viewModel = ViewModelProvider(this)[VerificationViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    /**
     * UI bileşenlerini yapılandırır
     */
    private fun setupUI() {
        // Doğrula butonu
        binding.btnVerify.setOnClickListener {
            verifyParticipant()
        }
        
        // Ana menüye dön butonu
        binding.btnGoToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        
        // Registration ekranına geç butonu
        binding.btnGoToRegistration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
    
    /**
     * ViewModel LiveData'larını gözlemler
     */
    private fun observeViewModel() {
        // Katılımcı bilgisini gözle
        viewModel.participant.observe(this) { participant ->
            if (participant != null) {
                displayParticipantInfo(participant)
            }
        }
        
        // Doğrulama durumunu gözle
        viewModel.verificationStatus.observe(this) { status ->
            when (status) {
                is VerificationViewModel.VerificationStatus.NotFound -> {
                    // Kullanıcı bulunamadı - Kırmızı arka plan
                    showNotFound()
                }
                is VerificationViewModel.VerificationStatus.Found -> {
                    // Kullanıcı bulundu - Tip'e göre renk ayarla
                    setBackgroundColorByType(status.registrationType)
                }
                is VerificationViewModel.VerificationStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                    resetUI()
                }
            }
        }
    }
    
    /**
     * Katılımcı doğrulama işlemini başlatır
     */
    private fun verifyParticipant() {
        val userIdText = binding.etSearchUserId.text.toString()
        val userId = userIdText.toIntOrNull()
        
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "Geçerli bir kullanıcı ID'si giriniz", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.verifyParticipant(userId)
    }
    
    /**
     * Bulunan katılımcı bilgilerini ekranda gösterir
     */
    private fun displayParticipantInfo(participant: com.example.afinal.data.Participant) {
        // Sonuç bölümünü göster
        binding.resultLayout.isVisible = true
        binding.tvErrorMessage.isVisible = false
        
        // İsim ve unvan bilgilerini göster
        binding.tvParticipantName.text = participant.fullName
        binding.tvParticipantTitle.text = participant.title
        
        // Fotoğrafı Glide ile göster (eğer varsa)
        participant.photoPath?.let { photoPath ->
            try {
                // URI string'ini Uri nesnesine dönüştür
                val photoUri = Uri.parse(photoPath)
                Glide.with(this)
                    .load(photoUri)
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .error(android.R.drawable.ic_menu_camera)
                    .into(binding.imageViewParticipantPhoto)
            } catch (e: Exception) {
                // URI parse edilemezse veya dosya bulunamazsa varsayılan görsel göster
                binding.imageViewParticipantPhoto.setImageResource(android.R.drawable.ic_menu_camera)
            }
        } ?: run {
            // Fotoğraf yoksa varsayılan görsel göster
            binding.imageViewParticipantPhoto.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }
    
    /**
     * Kullanıcı bulunamadı durumunu gösterir (Kırmızı arka plan)
     */
    private fun showNotFound() {
        binding.resultLayout.isVisible = false
        binding.tvErrorMessage.isVisible = true
        binding.tvErrorMessage.text = "Kullanıcı bulunamadı!"
        binding.mainLayout.setBackgroundColor(getColor(R.color.error_red))
    }
    
    /**
     * Kayıt tipine göre arka plan rengini ayarlar
     * Type 1 (Full): GREEN
     * Type 2 (Student): BLUE
     * Type 3 (None): ORANGE
     */
    private fun setBackgroundColorByType(registrationType: Int) {
        val colorRes = when (registrationType) {
            1 -> R.color.success_green  // Full - Yeşil
            2 -> R.color.info_blue      // Student - Mavi
            3 -> R.color.warning_orange // None - Turuncu
            else -> R.color.success_green
        }
        binding.mainLayout.setBackgroundColor(getColor(colorRes))
    }
    
    /**
     * UI'ı sıfırlar
     */
    private fun resetUI() {
        binding.resultLayout.isVisible = false
        binding.tvErrorMessage.isVisible = false
        binding.mainLayout.setBackgroundColor(getColor(R.color.white))
    }
}

