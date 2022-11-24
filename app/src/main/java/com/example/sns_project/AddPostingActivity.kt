package com.example.sns_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivityAddpostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class AddPostingActivity : AppCompatActivity() {
    private val REQUEST_GET_IMAGE = 105
    var storage: FirebaseStorage? = null //파이어베이스 객체를 담은 변수
    var photoUri : Uri? = null //사진 Uri를 담을 변수
    var auth : FirebaseAuth? = null //유저
    var firestore: FirebaseFirestore? = null //데이터베이스 파이어스토어
    private lateinit var binding: ActivityAddpostBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddpostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance() //파이어베이스 스토리지 가져오기
        auth = FirebaseAuth.getInstance() //파이어베이스 유저 가져오기
        firestore = FirebaseFirestore.getInstance() //파이어베이스 파이어스토어 가져오기

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                when(REQUEST_GET_IMAGE){
                    REQUEST_GET_IMAGE -> {
                        try{
                            photoUri = it.data?.data
                            binding.imageView.setImageURI(photoUri)
                        } catch (_:Exception){}
                    }
                }
            }
        }

        //Open the album
        binding.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launcher.launch(intent)
        }

        //add image upload event
        binding.uploadButton.setOnClickListener {
            Upload() //파이어베이스에 저장
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun Upload() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.continueWithTask() {task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl}?.addOnSuccessListener {
                uri ->
            Toast.makeText(this, "Upload Success",
                Toast.LENGTH_SHORT).show()

            val contentDTO = ContentDTO()

            //이미지 주소
            contentDTO.imageUrl = uri!!.toString()
            //유저의 UID
            contentDTO.uid = auth?.currentUser?.uid
            //게시물의 설명
            contentDTO.explain = binding.description.text.toString()
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
                Toast.makeText(this, "fail...",
                    Toast.LENGTH_SHORT).show()
            }
        }
}