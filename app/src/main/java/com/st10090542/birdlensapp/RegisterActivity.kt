package com.st10090542.birdlensapp


import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var btn: TextView
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoadingBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn = findViewById(R.id.alreadyHaveAccount)
        inputUsername = findViewById(R.id.inputPassword)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)
        mAuth = FirebaseAuth.getInstance()
        mLoadingBar = ProgressDialog(this)

        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {
            checkCredentials()
        }

        checkCredentials()

        btn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun checkCredentials() {
        val username = inputUsername.text.toString()
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()
        val confirmPassword = inputConfirmPassword.text.toString()

        if (username.isEmpty() || username.length < 7) {
            showError(inputUsername, "Your username is not valid!")
        } else if (email.isEmpty() || !email.contains("@")) {
            showError(inputEmail, "Email is not valid")
        } else if (password.isEmpty() || password.length < 7) {
            showError(inputPassword, "Password must be 7 characters")
        } else if (confirmPassword.isEmpty() || confirmPassword != password) {
            showError(inputConfirmPassword, "Password does not match!")
        } else {
            mLoadingBar.setTitle("Registration")
            mLoadingBar.setMessage("Please wait while checking your credentials")
            mLoadingBar.setCanceledOnTouchOutside(false)
            mLoadingBar.show()

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show()

                    mLoadingBar.dismiss()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showError(input: EditText, s: String) {
        input.error = s
        input.requestFocus()
    }
}
