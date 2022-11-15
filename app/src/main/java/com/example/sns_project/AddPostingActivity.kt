package com.example.sns_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_addpost.*
import java.text.SimpleDateFormat
import java.util.*



class AddPostingActivity : AppCompatActivity() {
    val REQUEST_GET_IMAGE = 105
    var storage: FirebaseStorage? = null //파이어베이스 객체를 담은 변수
    var photoUri : Uri? = null //사진 Uri를 담을 변수
    var auth : FirebaseAuth? = null //유저
    var firestore: FirebaseFirestore? = null //데이터베이스 파이어스토어

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpost)

        //Initiate storage
        storage = FirebaseStorage.getInstance() //파이어베이스 스토리지 가져오기
        auth = FirebaseAuth.getInstance() //파이어베이스 유저 가져오기
        firestore = FirebaseFirestore.getInstance() //파이어베이스 파이어스토어 가져오기

        //Open the album
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GET_IMAGE)
        }


        //add image upload event
        upload_button.setOnClickListener {
            Upload() //파이어베이스에 저장
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_GET_IMAGE -> {
                    try{
                        photoUri = data?.data
                        imageView.setImageURI(photoUri)
                    }catch (e:Exception){}
                }
            }
        }
    }

    fun Upload() {
        //make filename
        //val imgFileName = "IMAGE_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_.png"
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //file upload(promise)
        storageRef?.putFile(photoUri!!)?.continueWithTask() {task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl}?.addOnSuccessListener {
                uri ->
            //progress_bar.visibility = View.GONE

            Toast.makeText(this, "Upload Success",
                Toast.LENGTH_SHORT).show()

            val contentDTO = ContentDTO()

            //이미지 주소
            contentDTO.imageUrl = uri!!.toString()
            //유저의 UID
            contentDTO.uid = auth?.currentUser?.uid
            //게시물의 설명
            contentDTO.explain = description.text.toString()
            //유저의 아이디
            contentDTO.userId = auth?.currentUser?.email
            //게시물 업로드 시간
            contentDTO.timestamp = System.currentTimeMillis()

            //게시물을 데이터를 생성 및 엑티비티 종료
            firestore?.collection("images/")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)
            finish()
        }
            ?.addOnFailureListener {
                //progress_bar.visibility = View.GONE

                Toast.makeText(this, "fail...",
                    Toast.LENGTH_SHORT).show()
            }

        }


}