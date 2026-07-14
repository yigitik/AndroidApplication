package com.example.afinal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.databinding.ActivityMainBinding
import com.example.afinal.ui.RegistrationActivity
import com.example.afinal.ui.VerificationActivity

/**
 * MainActivity - Ana ekran
 * Kullanıcıları Registration ve Verification ekranlarına yönlendirir
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // ActionBar title'ı ayarla
        supportActionBar?.title = "Conference Registration System"
        
        setupNavigation()
    }
    
    /**
     * Navigation butonlarını yapılandırır
     */
    private fun setupNavigation() {
        // Module A: Registration Activity'ye git
        binding.btnRegistration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
        
        // Module B: Verification Activity'ye git
        binding.btnVerification.setOnClickListener {
            val intent = Intent(this, VerificationActivity::class.java)
            startActivity(intent)
        }
    }
}