package com.example.sns_project

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sns_project.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    private var auth : FirebaseAuth? = null
    private var currentUserUid : String? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_profile, container, false)
        val multiButton = fragmentView.findViewById<Button>(R.id.account_btn_follow_signout)
        val accountRecyclerview = fragmentView.findViewById<RecyclerView>(R.id.account_recyclerview)
        //?????? ???????????? ????????? ?????? ?????????
        firestore = FirebaseFirestore.getInstance() //?????????
        auth = FirebaseAuth.getInstance() // ?????????
        currentUserUid  = auth?.currentUser?.uid
        val prefs = requireActivity().getSharedPreferences("PREFS", 0)
        uid = prefs.getString("profileId", "none")

        //?????? ??????
        if(uid == currentUserUid){
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
            multiButton.setOnClickListener{
                requestFollow()
            }
        }

        accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        accountRecyclerview.layoutManager = GridLayoutManager(requireActivity(),3) // activity!! ?????? requireActivity ?????????

        //????????? ????????? ??????
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                when(PICK_PROFILE_FROM_ALBUM){
                    PICK_PROFILE_FROM_ALBUM -> {
                        try{
                            photoUri = it.data?.data
                            binding.accountIvProfile.setImageURI(photoUri)
                        } catch (_:Exception){}
                    }
                }
            }
            //other user page
            multiButton.text = getString(R.string.follow)
        }
        accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        accountRecyclerview.layoutManager = GridLayoutManager(requireActivity(),3) // activity!! ?????? requireActivity ?????????
        val accountIvProfile = fragmentView.findViewById(R.id.account_iv_profile) as ImageView
        //??????
        accountIvProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launcher.launch(intent)
        }
        getProfileImage()
        getFollowerAndFollowing()
        return fragmentView
    }

    private fun getFollowerAndFollowing(){
        //??????????????? ??????????????? ??? uid ?????? ????????? ???????????? ????????? ???????????? uid
        firestore?.collection("following")?.document(uid!!)?.addSnapshotListener { documentSnapshot, _ ->
            if(documentSnapshot == null) return@addSnapshotListener
            val followDTO = documentSnapshot.toObject(FollowDTO::class.java)
            if(followDTO?.followingCount != null){
                view?.findViewById<TextView>(R.id.account_tv_following_count)?.text = followDTO.followingCount.toString()
            }
            if(followDTO?.followerCount != null){
                view?.findViewById<TextView>(R.id.account_tv_follower_count)?.text = followDTO.followerCount.toString()
            }
            if (uid != currentUserUid){
                if(followDTO?.followers!!.containsKey(currentUserUid!!)){
                    view?.findViewById<Button>(R.id.account_btn_follow_signout)?.text = getString(R.string.follow_cancel)
                }else{
                    view?.findViewById<Button>(R.id.account_btn_follow_signout)?.text = getString(R.string.follow)
                }
            }
        }
    }

    private fun requestFollow(){
        //?????? ???????????? ????????? ????????? ?????? ???
        val tsDocFollowing = firestore?.collection("following")?.document(currentUserUid!!)
        firestore?.runTransaction{
                transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            //????????? ?????? ?????? ??????
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followers[uid!!] = true
                transaction.set(tsDocFollowing,followDTO)
                return@runTransaction
            }
            // ???????????? ??? ??????
            if(followDTO.followings.containsKey(uid)){
                // ????????? ????????? ?????? ??????.
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followings.remove(uid)
            }
            else{
                // ???????????? ??????.
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }

        // ?????? ????????? ??? ????????? ????????? ??????
        val tsDocFollower = firestore?.collection("following")?.document(uid!!)
        firestore?.runTransaction{
                transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true
                transaction.set(tsDocFollower,followDTO!!)
                return@runTransaction
            }
            //????????? ????????? ?????? ???????????? ?????? ??????
            if(followDTO!!.followers.containsKey(currentUserUid)){
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            }
            // ????????? ????????? ?????? ???????????? ?????? ????????? ??????
            else{
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
            }
            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }
    }

    private fun getProfileImage(){
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, _ ->
            if(documentSnapshot == null) return@addSnapshotListener
            if(documentSnapshot.data != null){
                val url = documentSnapshot.data!!["image"]
                Glide.with(requireActivity()).load(url).apply(RequestOptions().centerCrop()).into( view?.findViewById(R.id.account_iv_profile)!!)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        private var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        //?????????
        init { //????????? ???????????? ????????? ???????????? // ?????? ?????? ???????????? ?????? ?????? ?????????
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, _ ->
                if(querySnapshot == null) return@addSnapshotListener
                //????????? ????????????
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                //????????? ??????
                view?.findViewById<TextView>(R.id.account_tv_post_count)?.text = contentDTOs.size.toString()
                //????????????????????? ????????? ??? ??? ??????
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageView = ImageView(p0.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            val imageView = (p0 as CustomViewHolder).imageView
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }
}