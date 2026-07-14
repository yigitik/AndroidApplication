package com.example.afinal.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.example.afinal.databinding.ActivityRegistrationBinding
import com.example.afinal.ui.VerificationActivity
import com.example.afinal.viewmodel.RegistrationViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * RegistrationActivity - Module A: Katılımcı Kayıt Ekranı
 * Yeni kullanıcıların kayıt işlemlerini gerçekleştirir
 */
class RegistrationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var viewModel: RegistrationViewModel
    
    // Çekilen fotoğrafın URI'si
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null
    
    // Kamera izni kontrolü için launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Kamera izni verilmedi", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Kamera intent'i için launcher - FileProvider ile dosyaya kaydetme
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Fotoğraf dosyaya kaydedildi, URI'yi kullan
            photoUri?.let { uri ->
                binding.imageViewProfilePhoto.setImageURI(uri)
            } ?: run {
                Toast.makeText(this, "Fotoğraf yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Kullanıcı fotoğraf çekmeyi iptal etti veya hata oluştu
            photoUri = null
            currentPhotoPath = null
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // ViewModel'i başlat
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    /**
     * UI bileşenlerini yapılandırır
     */
    private fun setupUI() {
        // Unvan spinner'ını doldur
        val titleOptions = arrayOf("Prof.", "Dr.", "Student")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, titleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTitle.adapter = adapter
        
        // Konferans bilgileri butonu - WebView veya Browser Intent
        binding.btnConferenceInfo.setOnClickListener {
            openConferenceWebsite()
        }
        
        // Profil fotoğrafı ImageView tıklama
        binding.imageViewProfilePhoto.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
        
        // Kayıt butonu
        binding.btnRegister.setOnClickListener {
            registerParticipant()
        }
        
        // Ana menüye dön butonu
        binding.btnGoToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        
        // Verification ekranına geç butonu
        binding.btnGoToVerification.setOnClickListener {
            val intent = Intent(this, VerificationActivity::class.java)
            startActivity(intent)
        }
    }
    
    /**
     * ViewModel LiveData'larını gözlemler
     */
    private fun observeViewModel() {
        viewModel.registrationStatus.observe(this) { status ->
            when (status) {
                is RegistrationViewModel.RegistrationStatus.Success -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                    // Formu temizle
                    clearForm()
                }
                is RegistrationViewModel.RegistrationStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * Konferans web sitesini açar (Browser Intent)
     */
    private fun openConferenceWebsite() {
        try {
            val url = "https://zoom.us/tr/signin#/login"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            // Tarayıcı seçici ile aç
            val chooser = Intent.createChooser(intent, "Tarayıcı seçin")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                // Alternatif: Direkt URL açmayı dene
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Tarayıcı uygulaması bulunamadı. Lütfen cihazınızda bir tarayıcı yüklü olduğundan emin olun.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Web sitesi açılırken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Kamera iznini kontrol eder ve kamera uygulamasını açar
     */
    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    /**
     * Kamera uygulamasını açar - FileProvider kullanarak fotoğrafı dosyaya kaydeder
     */
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        
        try {
            // Fotoğraf için geçici dosya oluştur
            val photoFile = createImageFile()
            photoFile?.let { file ->
                // FileProvider ile URI oluştur
                photoUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                currentPhotoPath = file.absolutePath
                
                // Intent'e URI ekle
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                
                if (intent.resolveActivity(packageManager) != null) {
                    cameraLauncher.launch(intent)
                } else {
                    Toast.makeText(this, "Kamera uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Fotoğraf dosyası oluşturulamadı", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Kamera açılırken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
            photoUri = null
            currentPhotoPath = null
        }
    }
    
    /**
     * Fotoğraf için geçici dosya oluşturur
     */
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        
        val storageDir = File(filesDir, "photos")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }
    
    /**
     * Katılımcı kayıt işlemini gerçekleştirir
     */
    private fun registerParticipant() {
        val userIdText = binding.etUserId.text.toString()
        val fullName = binding.etFullName.text.toString().trim()
        val selectedTitle = binding.spinnerTitle.selectedItem.toString()
        val registrationType = when (binding.radioGroupRegistrationType.checkedRadioButtonId) {
            R.id.radioFull -> 1
            R.id.radioStudent -> 2
            R.id.radioNone -> 3
            else -> 1
        }
        
        // User ID validasyonu
        val userId = userIdText.toIntOrNull()
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "Geçerli bir kullanıcı ID'si giriniz", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Fotoğraf URI'sini string olarak al (eğer varsa)
        val photoUriString = photoUri?.toString() ?: currentPhotoPath
        
        // ViewModel'e kayıt işlemini başlat
        viewModel.registerParticipant(userId, fullName, selectedTitle, registrationType, photoUriString)
    }
    
    /**
     * Formu temizler
     */
    private fun clearForm() {
        binding.etUserId.text.clear()
        binding.etFullName.text.clear()
        binding.spinnerTitle.setSelection(0)
        binding.radioGroupRegistrationType.check(R.id.radioFull)
        binding.imageViewProfilePhoto.setImageResource(android.R.drawable.ic_menu_camera)
        photoUri = null
        currentPhotoPath = null
    }
}

