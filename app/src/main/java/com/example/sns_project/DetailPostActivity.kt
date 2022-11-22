package com.example.sns_project

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailPostActivity : AppCompatActivity() {
    lateinit var datas : ContentDTO
    lateinit var detail_image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailpost)

        detail_image = findViewById(R.id.di) as ImageView

        datas = intent.getSerializableExtra("data") as ContentDTO
        Glide.with(this).load(datas.imageUrl).into(detail_image)

        //detail_image = intent.getStringExtra("data", "clean")
    }
}