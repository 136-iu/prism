package com.example.prism.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 用 Android Keystore 加密 API Key
 * 加密结果格式：Base64(iv + ciphertext)
 */
object AesHelper {

    private const val KEYSTORE = "AndroidKeyStore"
    private const val ALIAS = "prism_key"
    private const val TRANSFORM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH = 128

    private fun getKey(): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (ks.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry)?.let {
            return it.secretKey
        }
        val kg = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, KEYSTORE
        )
        kg.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return kg.generateKey()
    }

    fun encrypt(plain: String): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORM)
            cipher.init(Cipher.ENCRYPT_MODE, getKey())
            val iv = cipher.iv
            val enc = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
            val combined = ByteArray(iv.size + enc.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(enc, 0, combined, iv.size, enc.size)
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (_: Exception) {
            // 降级：直接 Base64（不安全但至少不崩）
            Base64.encodeToString(plain.toByteArray(), Base64.NO_WRAP)
        }
    }

    fun decrypt(cipherText: String): String {
        return try {
            val combined = Base64.decode(cipherText, Base64.NO_WRAP)
            val iv = combined.copyOfRange(0, 12)
            val enc = combined.copyOfRange(12, combined.size)
            val cipher = Cipher.getInstance(TRANSFORM)
            cipher.init(
                Cipher.DECRYPT_MODE, getKey(),
                GCMParameterSpec(TAG_LENGTH, iv)
            )
            String(cipher.doFinal(enc), Charsets.UTF_8)
        } catch (_: Exception) {
            try {
                String(Base64.decode(cipherText, Base64.NO_WRAP), Charsets.UTF_8)
            } catch (_: Exception) { "" }
        }
    }
}