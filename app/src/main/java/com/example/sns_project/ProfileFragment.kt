package com.example.sns_project

import android.content.Intent
import android.media.session.PlaybackState
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sns_project.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_addpost.*
import kotlinx.android.synthetic.main.activity_login.view.*


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

    var fragmentView : View? = null
    lateinit var binding : FragmentProfileBinding
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var fragmentView =  inflater.inflate(R.layout.fragment_profile, container, false)
        //fragmentView?.account_tv_post_count?.text = "하위"
       // return fragmentView
        //이전 화면에서 넘어온 값을 받아옴



        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance() //초기화
        auth = FirebaseAuth.getInstance() // 초기화



        currentUserUid  = auth?.currentUser?.uid

        //나의 계정
        if(uid == currentUserUid){
        //mypage
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(
                    Intent(activity, LoginActivity::class.java))
                auth?.signOut()
            }
        }
        else{
            //other user  page
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)


            fragmentView?.account_btn_follow_signout?.setOnClickListener{
                requestFollow()
            }

        }



        fragmentView?.account_reyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_reyclerview?.layoutManager = GridLayoutManager(requireActivity(),3) // activity!! 대신 requireActivity 넣었음

        //프로필 이미지 변경

        //앨범
        fragmentView?.account_iv_profile?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activity?.startActivityForResult(intent, PICK_PROFILE_FROM_ALBUM)
        }

        getProfileImage()
        getFollowerAndFollwing()
        return fragmentView

    }

    fun getFollowerAndFollwing(){
        //내페이지를 입력햇을때 내 uid 이고 상대방 페이지를 누르면 상대방의 uid
        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot == null) return@addSnapshotListener
                 var followDTO  = documentSnapshot.toObject(FollowDTO::class.java)
            if(followDTO?.follwingCount != null){
                view?.findViewById<TextView>(R.id.account_tv_following_count)?.text = followDTO?.follwingCount?.toString()
            }
            if(followDTO?.follwerCount != null){
                view?.findViewById<TextView>(R.id.account_tv_follower_count)?.text = followDTO?.follwerCount?.toString()
                if(followDTO?.followers?.containsKey(currentUserUid!!)){
                    view?.findViewById<TextView>(R.id.account_btn_follow_signout)?.text = getString(R.string.follow_cancel)

                }else{
                    view?.findViewById<TextView>(R.id.account_btn_follow_signout)?.text = getString(R.string.follow)
                }
            }

        }
    }
    fun requestFollow(){
        //나의 계정에는 누구를 팔로우 하는 지
    var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction{
        transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            //팔로우 하지 않은 상태
            if(followDTO == null){
            followDTO = FollowDTO()
                followDTO!!.follwingCount = 1
                followDTO!!.followers[uid!!] = true

                transaction.set(tsDocFollowing,followDTO)
                return@runTransaction
            }
            // 팔로우를 한 상태
            if(followDTO.followings.containsKey(uid)){
                // 팔로우 취소를 하면 된다.
            followDTO?.follwingCount = followDTO?.follwingCount - 1
                followDTO?.followers?.remove(uid)

            }
            else{
                // 팔로윙을 한다.
                followDTO?.follwingCount = followDTO?.follwingCount + 1
                followDTO?.followers[uid!!] = true
            }
            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }

        // 내가 팔로잉 할 상대방 계정의 접근
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction{
            transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.follwerCount = 1
                followDTO!!.followers[currentUserUid!!] = true

                transaction.set(tsDocFollower,followDTO!!)
                return@runTransaction
            }
            //상대방 계정에 내가 팔로우를 했을 경우
            if(followDTO!!.followers.containsKey(currentUserUid)){
                followDTO!!.follwerCount = followDTO!!.follwerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)

            }
            // 상대방 계정에 내가 팔로우를 하지 않았을 경우
            else{
                followDTO!!.follwerCount = followDTO!!.follwerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
            }
            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }



    }


    fun getProfileImage(){
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            if(documentSnapshot.data != null){
                var url = documentSnapshot?.data!!["image"]

                Glide.with(requireActivity()).load(url).apply(RequestOptions().centerCrop()).into( view?.findViewById<ImageView>(R.id.account_iv_profile)!!)


            }
        }
    }
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        //var fragmentView =  inflater.inflate(R.layout.fragment_profile, container, false)

        //생성자
        init { //데이터 베이스의 값들을 읽어오기 // 내가 올린 이미지만 뜨게 할수 있도록
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
               if(querySnapshot == null) return@addSnapshotListener
                //데이터 가져오기
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)

                }
                //포스트 갯수
                    //
               // fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                view?.findViewById<TextView>(R.id.account_tv_post_count)?.text = contentDTOs.size.toString()

                //리사이클러뷰가 최신화 할 수 있게
                notifyDataSetChanged()

            }
        }
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
          var width = resources.displayMetrics.widthPixels / 3
            var imageView = ImageView(p0.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        var imageView = (p0 as CustomViewHolder).imageView
        Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
        }

        override fun getItemCount(): Int {
           return contentDTOs.size
        }



    }


    companion object {
       var PICK_PROFILE_FROM_ALBUM = 10;
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