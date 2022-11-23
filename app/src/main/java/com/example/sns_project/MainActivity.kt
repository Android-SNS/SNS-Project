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

        userCollection.document(email!!).update("uid", FirebaseAuth.getInstance().currentUser?.uid)
            .addOnSuccessListener {
                }.addOnFailureListener {  }

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        //supportFragmentManager.beginTransaction().add(R.id.fl_con, NaviHomeFragment()).commit()

        bnv_main.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.home -> {
                        //bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_bnv1)
                        //bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.color_bnv1)
                        val homeFragment = HomeFragment()
                        val bundle = Bundle()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        bundle.putString("destinationUid", uid)
                        homeFragment.arguments = bundle
                        homeFragment
                        // Respond to navigation item 1 click
                    }
                    R.id.search -> {
                        //bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        //bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        val searchFragment = SearchFragment()
                        val bundle = Bundle()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        bundle.putString("destinationUid", uid)
                        searchFragment.arguments = bundle
                        searchFragment
                        // Respond to navigation item 2 click
                    }
                    R.id.profile -> {
                        //bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        //bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        val editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        editor.putString("profileId", uid)
                        editor.apply()
                        ProfileFragment()
                        // Respond to navigation item 3 click
                    }
                    else -> {
                        //bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_bnv1)
                        //bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.color_bnv1)
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