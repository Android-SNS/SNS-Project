package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
    var uid : String? = null
    var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.setHasFixedSize(true)

        uid = arguments?.getString("destinationUid")
        auth = FirebaseAuth.getInstance()

        val adapter = SearchAdapter()
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(context)

        val searchBar = view.findViewById<EditText>(R.id.searchuser)
        val searchbtn = view.findViewById<Button>(R.id.searchbtn)

        searchBar.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

        searchbtn.setOnClickListener {
            if(searchBar.text.isNotEmpty()) {
                val userId = searchBar.text.toString()
                val db = Firebase.firestore
                val userCollection = db.collection("users")
                contentDTOs.clear()
                userCollection.whereEqualTo("userId", userId).addSnapshotListener { querySnapshot, _ ->
                    if(querySnapshot == null) return@addSnapshotListener
                    //데이터 가져오기
                    for(snapshot in querySnapshot.documents){
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return view
    }

    inner class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

        private val firestore = FirebaseFirestore.getInstance()
        private var currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val user = contentDTOs[position]
            val followbtn = holder.itemView.findViewById<Button>(R.id.btn_follow)

            holder.itemView.findViewById<TextView>(R.id.username).text = user.userId
            holder.itemView.findViewById<TextView>(R.id.fullname).text = user.nickname
            isFollowing(user.uid!!, holder.itemView.findViewById(R.id.btn_follow))

            if(user.uid?.equals(arguments?.getString("destinationUid")) == true)
                followbtn.visibility = View.GONE
            else
                followbtn.visibility = View.VISIBLE

            holder.itemView.setOnClickListener {
                val editor = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileId", user.uid)
                editor.apply()

                activity!!.supportFragmentManager.beginTransaction().
                replace(R.id.fl_container, ProfileFragment()).commit()
            }

            followbtn.setOnClickListener {
                val tsDocFollowing = firestore.collection("following").document(currentUserUid)
                val tsDocFollower = firestore.collection("following").document(user.uid!!)

                firestore.runTransaction{ transaction ->
                    var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
                    //팔로우 하지 않은 상태
                    if(followDTO == null){
                        followDTO = FollowDTO()
                        followDTO.follwingCount = 1
                        followDTO.followers[user.uid!!] = true
                        transaction.set(tsDocFollowing,followDTO)
                        return@runTransaction
                    }
                    // 팔로우를 한 상태
                    if(followDTO.followings.containsKey(user.uid)){
                        // 팔로우 취소를 하면 된다.
                        followDTO.follwingCount = followDTO.follwingCount - 1
                        followDTO.followings.remove(user.uid)
                    } else{
                        // 팔로윙을 한다.
                        followDTO.follwingCount = followDTO.follwingCount + 1
                        followDTO.followings[user.uid!!] = true
                    }
                    transaction.set(tsDocFollowing,followDTO)
                    return@runTransaction
                }
                // 내가 팔로잉 할 상대방 계정의 접근
                firestore.runTransaction{ transaction ->
                    var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)
                    if(followDTO == null){
                        followDTO = FollowDTO()
                        followDTO!!.follwerCount = 1
                        followDTO!!.followers[currentUserUid] = true
                        transaction.set(tsDocFollower,followDTO!!)
                        return@runTransaction
                    }
                    //상대방 계정에 내가 팔로우를 했을 경우
                    if(followDTO!!.followers.containsKey(currentUserUid)){
                        followDTO!!.follwerCount = followDTO!!.follwerCount - 1
                        followDTO!!.followers.remove(currentUserUid)
                    }
                    // 상대방 계정에 내가 팔로우를 하지 않았을 경우
                    else{
                        followDTO!!.follwerCount = followDTO!!.follwerCount + 1
                        followDTO!!.followers[currentUserUid] = true
                    }
                    transaction.set(tsDocFollower,followDTO!!)
                    return@runTransaction
                }
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        private fun isFollowing(uid: String, button: Button) {
            firestore.collection("following").document(uid)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    val followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                    if (followDTO?.followers!!.containsKey(currentUserUid)) {
                        button.text = getString(R.string.follow_cancel)
                    } else {
                        button.text = getString(R.string.follow)
                    }
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}