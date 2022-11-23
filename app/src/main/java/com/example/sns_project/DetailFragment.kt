package com.example.sns_project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNREACHABLE_CODE")
class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var contentDTOs : ArrayList<ContentDTO> = arrayListOf() //게시물 담을 배열
    var uidList : ArrayList<String> = arrayListOf() //사용자의 uid를 담을 배열

    var uid : String? = null
    var firestore : FirebaseFirestore? = null //DB접근
    var auth : FirebaseAuth? = null
    var currentUserUid : String? = null
    var detailView : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val detailView = inflater.inflate(R.layout.fragment_detail, container, false)
        val recyclerView = detailView.findViewById<RecyclerView>(R.id.detail_recyclerview)
        //recyclerView.setHasFixedSize(true)


        firestore = FirebaseFirestore.getInstance() //초기화
        auth = FirebaseAuth.getInstance() // 초기화
        currentUserUid  = auth?.currentUser?.uid
        val prefs = requireActivity().getSharedPreferences("PREFS", 0)
        uid = prefs.getString("profileId", "none")

        recyclerView.adapter = DetailViewRecyclerviewAdapter()
        return detailView
    }

//    override fun onResume() {
//        super.onResume()
//        detailView?.detail_recyclerview?.layoutManager = LinearLayoutManager(activity)
//        detailView?.detail_recyclerview?.adapter = DetailViewRecyclerviewAdapter()
//        //var mainActivity = activity as MainActivity
//        //mainActivity.progress_bar.visibility = View.INVISIBLE
//
//    }
//
//    override fun onStop() {
//        super.onStop()
//        imagesSnapshot?.remove()
//    }

    @SuppressLint("NotifyDataSetChanged")
    inner class DetailViewRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        val contentDTOs : ArrayList<ContentDTO> = arrayListOf() //ContentDTO를 담는 리스트
        val contentUidList : ArrayList<String> = arrayListOf() //Uid를 담는 리스트

        init {
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents) {
                    val item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()

            }

        }

        /*init {

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userDTO = task.result.toObject(FollowDTO::class.java)
                    if (userDTO?.followings != null) {
                        getCotents(userDTO?.followings)
                    }
                }
            }
        }
*/
        /*fun getCotents(followers: MutableMap<String, Boolean>?) {
            imagesSnapshot = firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)!!
                    println(item.uid)
                    if (followers?.keys?.contains(item.uid)!!) {
                        contentDTOs.add(item)
                        contentUidList.add(snapshot.id)
                    }
                }
                notifyDataSetChanged()
            }

        }*/



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_detail, parent, false)
            return RecyclerView.ViewHolder(view)
        }

        //inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) { //서버에서 넘어온 데이터를 매핑시켜주기
            //val viewHolder = (holder as CustomViewHolder).itemView


            //UserId
            holder.itemView.findViewById<TextView>(R.id.profile_username).text = contentDTOs[position].userId
            //viewHolder.profile_username.text = contentDTOs[position].userId

            //Image
            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                //.into(viewHolder.imageview_content)
                .into(holder.itemView.findViewById<ImageView>(R.id.imageview_content))

            //description
            //viewHolder.description_textview.text = contentDTOs[position].explain
            holder.itemView.findViewById<TextView>(R.id.description_textview).text = contentDTOs[position].explain
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}