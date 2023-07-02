package com.example.noteapp.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentSearchBinding
import com.example.noteapp.utils.hideKeyboard


class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding? = null
    private val binding:FragmentSearchBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().hideKeyboard()
        binding.edtSearch.requestFocus()
    }



}