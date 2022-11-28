package com.example.sns_project

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bnv_main = findViewById<BottomNavigationView>(R.id.bnv_main)
        val email = FirebaseAuth.getInstance().currentUser?.email
        val db = Firebase.firestore
        val userCollection = db.collection("users")
        val itemMap = hashMapOf(
            "login" to true
        )

        userCollection.whereEqualTo("firstLogin", 0).addSnapshotListener { querySnapshot, _ ->
            if(querySnapshot == null) return@addSnapshotListener
            val userDTO = querySnapshot.toObjects(UserDTO::class.java)
            for (i in 0 until userDTO.size){
                if (userDTO[i].firstLogin == 0 && userDTO[i].userId == email){
                    db.collection("following").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .set(itemMap)
                    userCollection.document(email!!).update("firstLogin", 1)
                }
            }
        }// 팔로우 관계 설정 위한 document 만들기 위한 함수

        userCollection.document(email!!).update("uid", FirebaseAuth.getInstance().currentUser?.uid)
            .addOnSuccessListener {
            }.addOnFailureListener {  }

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        bnv_main.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.home -> {
                        val homeFragment = HomeFragment()
                        val bundle = Bundle()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        bundle.putString("destinationUid", uid)
                        homeFragment.arguments = bundle
                        homeFragment
                    }
                    R.id.search -> {
                        val searchFragment = SearchFragment()
                        val bundle = Bundle()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        bundle.putString("destinationUid", uid)
                        searchFragment.arguments = bundle
                        searchFragment
                    }
                    R.id.profile -> {
                        val editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        editor.putString("profileId", uid)
                        editor.apply()
                        ProfileFragment()
                    }
                    else -> {
                        HomeFragment()
                    }
                }
            )
            true
        }
        bnv_main.selectedItemId = R.id.home
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}