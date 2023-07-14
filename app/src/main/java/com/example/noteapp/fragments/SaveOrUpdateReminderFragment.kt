package com.example.noteapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentSaveOrUpdateReminderBinding


class SaveOrUpdateReminderFragment : Fragment(R.layout.fragment_save_or_update_reminder) {

    private var _binding : FragmentSaveOrUpdateReminderBinding?  = null
    private val binding : FragmentSaveOrUpdateReminderBinding
        get() = _binding!!

    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       _binding = FragmentSaveOrUpdateReminderBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setOnClick()
    }

    private fun setOnClick(){
        binding.backBtn.setOnClickListener {
            navController.navigate(SaveOrUpdateReminderFragmentDirections
                .actionSaveOrUpdateReminderFragmentToRemindersFragment())
        }

        binding.saveNote.setOnClickListener {
            saveReminder()
        }
    }

    private fun saveReminder(){
        //TODO: Save Reminder
    }

}