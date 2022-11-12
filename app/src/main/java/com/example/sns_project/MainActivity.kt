package com.example.sns_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bnv_main = findViewById<BottomNavigationView>(R.id.bnv_main)

        //supportFragmentManager.beginTransaction().add(R.id.fl_con, NaviHomeFragment()).commit()

        bnv_main.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.home -> {
                        //bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_bnv1)
                        //bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.color_bnv1)
                        HomeFragment()
                        // Respond to navigation item 1 click
                    }
                    R.id.search -> {
                        //bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        //bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        SearchFragment()
                        // Respond to navigation item 2 click
                    }
                    R.id.profile -> {
                        //bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        //bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.color_bnv2)
                        var profileFragment = ProfileFragment()
                        var bundle = Bundle()
                        var uid = FirebaseAuth.getInstance().currentUser?.uid
                        bundle.putString("destinationUid",uid)
                        profileFragment.arguments = bundle
                        profileFragment
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
            .commit()
    }

}