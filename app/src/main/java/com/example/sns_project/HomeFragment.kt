package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var uid : String? = null
    private var auth : FirebaseAuth? = null

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
                for (key in followDTO?.followings!!.keys) {
                    firestore.collection("images").whereEqualTo("uid", key).addSnapshotListener { querySnapshot, _ ->
                        if (querySnapshot == null) return@addSnapshotListener
                        for (snapshot in querySnapshot.documents) {
                            contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                        }
                        notifyDataSetChanged()
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
            holder.itemView.findViewById<TextView>(R.id.publisher).text = postings.nickname
            holder.itemView.findViewById<TextView>(R.id.description).text = postings.explain
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }

    companion object
}