package com.example.afinal.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Participant Entity - Konferans katılımcı bilgilerini temsil eden Room Entity sınıfı
 * @param userId Benzersiz kullanıcı ID'si (Primary Key)
 * @param fullName Katılımcının tam adı
 * @param title Unvan (Prof., Dr., Student)
 * @param registrationType Kayıt tipi (1-Full, 2-Student, 3-None)
 * @param photoPath Profil fotoğrafının dosya yolu veya BLOB verisi
 */
@Entity(tableName = "participants")
data class Participant(
    @PrimaryKey
    val userId: Int,
    val fullName: String,
    val title: String,
    val registrationType: Int, // 1-Full, 2-Student, 3-None
    val photoPath: String? = null // Fotoğraf yolu veya base64 encoded string
)

