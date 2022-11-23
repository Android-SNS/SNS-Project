package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    var currentUserUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView =  inflater.inflate(R.layout.fragment_profile, container, false)
        val multiButton = fragmentView.findViewById<Button>(R.id.account_btn_follow_signout)
        val accountRecyclerview = fragmentView.findViewById<RecyclerView>(R.id.account_recyclerview)
        //이전 화면에서 넘어온 값을 받아옴
        firestore = FirebaseFirestore.getInstance() //초기화
        auth = FirebaseAuth.getInstance() // 초기화
        currentUserUid = auth?.currentUser?.uid
        val prefs = requireActivity().getSharedPreferences("PREFS", 0)
        uid = prefs.getString("profileId", "none")

        //나의 계정
        if(uid == currentUserUid){
            //my page
            multiButton.text = getString(R.string.signout)
            multiButton.setOnClickListener {
                activity?.finish()
                startActivity(
                    Intent(activity, LoginActivity::class.java))
                auth?.signOut()
            }
        }
        else{
            //other user page
            multiButton.text = getString(R.string.follow)
        }
        accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        accountRecyclerview.layoutManager = GridLayoutManager(requireActivity(),3) // activity!! 대신 requireActivity 넣었음
        return fragmentView
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        //생성자
        init { //데이터 베이스의 값들을 읽어오기 // 내가 올린 이미지만 뜨게 할수 있도록
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener
                //데이터 가져오기
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                //포스트 개수 불러오기
                view?.findViewById<TextView>(R.id.account_tv_post_count)?.text = contentDTOs.size.toString()
                //리사이클러뷰가 최신화 할 수 있게
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageView = ImageView(p0.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            val imageView = (p0 as CustomViewHolder).imageView
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)

            //클릭 이벤트
//            val nextIntent = Intent(this, DetailPostActivity::class.java)
//            nextIntent.putExtra("data", imageView)
//            startActivity(nextIntent)

            /*
            p0.itemView.setOnClickListener {
                System.out.println("click")
                //activity?.finish()
                //startActivity(
                Intent(context, DetailPostActivity::class.java).apply {
                    putExtra("data", )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run {
                    context?.startActivity(this)
                }
                /*val intent = Intent(this, DetailPostActivity::class.java)
                intent.putExtra("data","https://firebasestorage.googleapis.com/v0/b/android-kotlin-86af2.appspot.com/o/images%2FJPEG_20221115_033230_.png?alt=media&token=7c6c1afd-de2c-4ad0-88f2-aa612a351dcd")

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)*/
            }

            */

        }



        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}