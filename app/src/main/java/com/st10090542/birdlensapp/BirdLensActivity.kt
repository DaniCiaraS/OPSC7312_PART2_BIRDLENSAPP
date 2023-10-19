package com.st10090542.birdlensapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class BirdLensActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bird_lens)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@BirdLensActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}