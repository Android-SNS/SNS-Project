package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//import com.example.sns_project.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val uploadView = inflater.inflate(R.layout.fragment_home, container, false)
        val fButton = uploadView.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val homeView = uploadView.findViewById<RecyclerView>(R.id.homeView)
        homeView.setHasFixedSize(true)

        uid = arguments?.getString("destinationUid")
        auth = FirebaseAuth.getInstance()

        val adapter = HomeAdapter()
        homeView.adapter = adapter
        homeView.layoutManager = LinearLayoutManager(context)

        fButton.setOnClickListener {
            activity?.finish()
            startActivity(
                Intent(activity, AddPostingActivity::class.java))
        }
        return uploadView
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        private var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

        private val firestore = FirebaseFirestore.getInstance()

        init {
            firestore.collection("following").document(uid!!).addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot == null) return@addSnapshotListener
                val followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                if (!followDTO?.followings!!.containsKey(uid)) {
                    println(followDTO.followings.keys)
                    for (i in followDTO.followings.keys) {
                        firestore.collection("images").whereEqualTo("uid", i)
                            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                if (querySnapshot == null) return@addSnapshotListener
                                for (snapshot in querySnapshot.documents) {
                                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                                }
                            }
                    }
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val postings = contentDTOs[position]
            val width = resources.displayMetrics.widthPixels
            val imageView = (holder.itemView.findViewById(R.id.post_image) as ImageView)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
            holder.itemView.findViewById<TextView>(R.id.username).text = postings.userId
            holder.itemView.findViewById<TextView>(R.id.publisher).text = postings.userId
            holder.itemView.findViewById<TextView>(R.id.description).text = postings.explain
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}