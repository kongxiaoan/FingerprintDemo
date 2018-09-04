package com.kpa.fingerprintdemo

import android.app.KeyguardManager
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import java.security.KeyStore
import javax.crypto.KeyGenerator
import android.security.keystore.KeyGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey


class MainActivity : AppCompatActivity() {
    private lateinit var keyStore: KeyStore
    private val DEFAULT_KEY_NAME = "default_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        if (intent.getBooleanExtra("isSuccess", false)) {
            WelcomeActivity.startActivity(this)
            finish()
            FingerprintManager
        } else {
            //判断是否支持该功能
            if (supportFingerprint()) {
                initKey() //生成一个对称加密的key
                initCipher() //生成一个Cipher对象
            }
        }
    }

    private fun initCipher() {
        val key = keyStore.getKey(DEFAULT_KEY_NAME, null) as SecretKey
        val cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        showFingerPrintDialog(cipher)
    }

    private fun showFingerPrintDialog(cipher: Cipher?) {
        val fingerprintDialog = FingerprintDialog()
        if (cipher != null) {
            fingerprintDialog.setCipher(cipher)
        }
        fingerprintDialog.show(supportFragmentManager, "fingerprint")
    }

    private fun initKey() {
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val builder = KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    private fun supportFingerprint(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            Toast.makeText(this, "系统不支持指纹功能", Toast.LENGTH_SHORT).show()
            return false
        } else {
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            val managerCompat = FingerprintManagerCompat.from(this)
            if (!managerCompat.isHardwareDetected) {
                Toast.makeText(this, "系统不支持指纹功能", Toast.LENGTH_SHORT).show()
                return false
            } else if (!keyguardManager.isKeyguardSecure) {
                Toast.makeText(this, "屏幕未设置锁屏 请先设置锁屏并添加一个指纹", Toast.LENGTH_SHORT).show()
                return false
            } else if (!managerCompat.hasEnrolledFingerprints()) {
                Toast.makeText(this, "至少在系统中添加一个指纹", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    companion object {
        fun startActivity(activity: MainActivity, isSuccess: Boolean) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("isSuccess", isSuccess)
            activity.startActivity(intent)
        }
    }
}
