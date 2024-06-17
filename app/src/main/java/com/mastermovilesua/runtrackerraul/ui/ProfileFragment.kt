package com.mastermovilesua.runtrackerraul.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mastermovilesua.runtrackerraul.LoginActivity
import com.mastermovilesua.runtrackerraul.MainActivity
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {


    private lateinit var binding: FragmentProfileBinding;

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.name.text = currentUser?.displayName
        binding.email.text = currentUser?.email


        binding.closeSession.setOnClickListener {
            auth.signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(intent)
            requireActivity().finish()
        }
    }

}