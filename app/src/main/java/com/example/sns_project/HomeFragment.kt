package com.example.sns_project

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val uploadView = inflater.inflate(R.layout.fragment_home, container, false)
        //Intent addPostingActivity = new Intent(getActivity(), addPostingActivity.class)

//        ActivityCompat.requestPermissions(activity,
//            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        val fButton = uploadView.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        fButton.setOnClickListener {
            activity?.finish()
            startActivity(
                Intent(activity, AddPostingActivity::class.java))
//            if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                startActivity(
//                    Intent(activity, AddPostingActivity::class.java)
//                )
//            }
        }

        return uploadView
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
//        val binding = fragmentHomeBinding
//
//        initFloatingButton(view)
//
//        binding!!.addFloatingButton
//    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
//        fragmentHomeBinding.floatingActionButton.setOnClickListener {
//            startActivity(Intent(this, addPostingActivity::class.java))
//        }
//
//
//    }


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