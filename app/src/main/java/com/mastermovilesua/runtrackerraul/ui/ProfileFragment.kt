package com.mastermovilesua.runtrackerraul.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {


    private lateinit var binding: FragmentProfileBinding;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}