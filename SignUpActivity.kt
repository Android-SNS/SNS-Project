package com.example.firebaseex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseex.databinding.ActivitySignupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submit.setOnClickListener {
            if (binding.password.length() < 6) {
                Toast.makeText(this, "비밀번호는 6자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val userEmail = binding.username.text.toString()
                val password = binding.password.text.toString()
                addUser(userEmail, password)
            }
        }
    }

    private fun addUser(userEmail: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                startActivity(
                    Intent(this, LoginActivity::class.java))
                Toast.makeText(this, "${userEmail}계정을 만들었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}