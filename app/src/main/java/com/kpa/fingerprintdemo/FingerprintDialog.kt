package com.kpa.fingerprintdemo

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.fragment.app.DialogFragment
import javax.crypto.Cipher

class FingerprintDialog : DialogFragment {
    constructor() : super()

    private lateinit var mActivity: MainActivity
    private lateinit var fingerprintManagerCompat: FingerprintManagerCompat
    private lateinit var errorMsg: TextView
    private lateinit var cancel: TextView
    private lateinit var mCancellationSignal: CancellationSignal
    // 标识用户是否是主动取消的认证

    fun setCipher(cipher: Cipher) {
        mCipher = cipher
    }

    private var isSelfCancelled = false
    private lateinit var mCipher: Cipher

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = activity as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fingerprintManagerCompat = FingerprintManagerCompat.from(context!!)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fingerprint_fragment, container, false)
        errorMsg = view.findViewById(R.id.fingerprint_error_tv)
        cancel = view.findViewById(R.id.fingerprint_cancel_tv)
        cancel.setOnClickListener {
            dismiss()
            stopListening()
            mActivity.finish()
        }
        return view
    }

    /**
     * 停止指纹监听
     */
    private fun stopListening() {
        if (null != mCancellationSignal) {
            mCancellationSignal.cancel()
            isSelfCancelled = true
        }
    }

    override fun onResume() {
        super.onResume()
        startListening()
    }

    /**
     * 开始指纹监听
     */
    @SuppressLint("MissingPermission")
    private fun startListening() {
        isSelfCancelled = false
        mCancellationSignal = CancellationSignal()
        fingerprintManagerCompat.authenticate(FingerprintManagerCompat.CryptoObject(mCipher), 0, mCancellationSignal, object : FingerprintManagerCompat.AuthenticationCallback() {
            override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                if (!isSelfCancelled) {
                    errorMsg.text = errString
                    if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show()
                        dismiss()
                        mActivity.finish()
                    }
                }
            }

            override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                MainActivity.startActivity(mActivity, true)
            }

            override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                errorMsg.text = helpString
            }

            override fun onAuthenticationFailed() {
                errorMsg.text = "指纹验证失败，请重试"
            }
        }, null)

    }


    /**
     * 指纹传感器和摄像头一样 不能多个应用同时使用
     */
    override fun onPause() {
        super.onPause()
        stopListening()
    }

}


