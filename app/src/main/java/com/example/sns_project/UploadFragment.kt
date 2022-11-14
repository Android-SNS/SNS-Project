package com.example.sns_project

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_upload.*
import kotlinx.android.synthetic.main.fragment_upload.view.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var viewUpload: View? = null
    var pickImageFromAlbum = 0
    var fbStorage : FirebaseStorage? = null
    var fireStore: FirebaseFirestore? = null
    var uriPhoto : Uri? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewUpload = inflater.inflate(R.layout.fragment_upload, container, false)

        // Initiallize Firebase Stroge
        fbStorage = FirebaseStorage.getInstance()
        fireStore = FirebaseFirestore.getInstance()


//        viewUpload!!.imageView.setOnClickListener {
//
//            // Open Album
//            val photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
//        }
         //Open Album
        imageView2.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
        }

        upload_button2.setOnClickListener {
            ImageUpload(viewUpload!!)
        }
        return viewUpload
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == pickImageFromAlbum) { //이미지 선택 시
            if(resultCode == Activity.RESULT_OK) {
                 // Path for the selected image
                uriPhoto = data?.data
                imageView2.setImageURI(uriPhoto)

                if(ContextCompat.checkSelfPermission(viewUpload!!.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ImageUpload(viewUpload!!)
                }
                else {

                }
            }
        }
    }

//    fun ImageUpload(view: View){
//        progress_bar.visibility = View.VISIBLE
//
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_.png"
//        val imgFileName = "IMAGE_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_.png"
//        val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)
//        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener { taskSnapshot ->
//            progress_bar.visibility = View.GONE
//            Toast.makeText(view.context, "Image Uploaded", Toast.LENGTH_SHORT).show()
//
//            val uri = taskSnapshot.downloadUrl
//            //디비에 바인딩 할 위치 생성 및 컬렉션(테이블)에 데이터 집합 생성
//
//
//            //시간 생성
//            val contentDTO = ContentDTO()
//
//            //이미지 주소
//            contentDTO.imageUrl = uri!!.toString()
//            //유저의 UID
//            contentDTO.uid = auth?.currentUser?.uid
//            //게시물의 설명
//            contentDTO.explain = description.text.toString()
//            //유저의 아이디
//            contentDTO.userId = auth?.currentUser?.email
//            //게시물 업로드 시간
//            contentDTO.timestamp = System.currentTimeMillis()
//
//            //게시물을 데이터를 생성 및 엑티비티 종료
//            fireStore?.collection("images")?.document()?.set(contentDTO)
//
//            //setResult(Activity.RESULT_OK)
//            //finish()
//        }
//            ?.addOnFailureListener {
//                //progress_bar.visibility = View.GONE
//
////                Toast.makeText(this, getString(R.string.upload_fail),
////                    Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun ImageUpload(view: View) {
        //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imgFileName = "IMAGE_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_.png"

        //val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)
        val storageRef = fbStorage?.reference?.child("images/")?.child(imgFileName)
        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            Toast.makeText(view.context, "Image Uploaded", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                UploadFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

}