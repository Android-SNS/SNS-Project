package com.example.sns_project

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivitySignupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
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
                val nickname = binding.nickname.text.toString()
                addUser(userEmail, password, nickname)
            }
        }
    }

    private fun addUser(userEmail: String, password: String, nickname: String) {
        val db = Firebase.firestore
        val userCollection = db.collection("users")
        val itemMap = hashMapOf(
            "userId" to userEmail,
            "nickname" to nickname
        )

        userCollection.document(userEmail).set(itemMap)
            .addOnSuccessListener {
            }.addOnFailureListener {  }
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                startActivity(
                    Intent(this, LoginActivity::class.java))
                Toast.makeText(this, "${userEmail}계정을 만들었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
