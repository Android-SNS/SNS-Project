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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.setHasFixedSize(true)

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
                val uid = searchBar.text.toString()
                val db = Firebase.firestore
                val userCollection = db.collection("users")
                contentDTOs.clear()
                userCollection.document(uid).get().addOnSuccessListener {
                    var nickname = it["nickname"].toString()
                    var user = ContentDTO(
                        null, null, uid, nickname, null, null, HashMap()
                    )
                    contentDTOs.add(user)
                }.addOnFailureListener {}
                adapter.notifyDataSetChanged()
            }
        }
        return view
    }

    inner class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view){

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val user = contentDTOs[position]

            holder.itemView.findViewById<TextView>(R.id.username).text = user.uid
            holder.itemView.findViewById<TextView>(R.id.fullname).text = user.userId
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
