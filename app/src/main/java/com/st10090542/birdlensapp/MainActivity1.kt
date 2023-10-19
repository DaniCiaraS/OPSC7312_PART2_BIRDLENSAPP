package com.st10090542.birdlensapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;



class MainActivity1 : AppCompatActivity() {

    private lateinit var btnLogout: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        btnLogout = findViewById(R.id.btnLogout)
        mAuth = FirebaseAuth.getInstance()

        btnLogout.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@MainActivity1, MainActivity1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
         }
       }
}