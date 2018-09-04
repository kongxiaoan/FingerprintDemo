package com.kpa.fingerprintdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    companion object {
        fun startActivity(context:Activity){
            context.startActivity(Intent(context,WelcomeActivity::class.java))
        }
    }
}
